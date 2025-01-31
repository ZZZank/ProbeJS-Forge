package zzzank.probejs.docs.bindings;

import dev.latvian.mods.rhino.*;
import lombok.val;
import zzzank.probejs.ProbeJS;
import zzzank.probejs.lang.transpiler.TypeConverter;
import zzzank.probejs.lang.typescript.code.type.BaseType;
import zzzank.probejs.lang.typescript.code.type.Types;
import zzzank.probejs.lang.typescript.code.type.js.JSObjectType;
import zzzank.probejs.lang.typescript.code.type.js.JSPrimitiveType;

import javax.annotation.Nullable;
import java.util.*;

/**
 * @author ZZZank
 */
public class ValueTypes {

    private static final Map<Class<?>, ValueTypeConverter> FORMATTERS =
        new LinkedHashMap<>();
    private static final Set<Class<?>> PRIMITIVES = new HashSet<>();

    static {
        PRIMITIVES.addAll(Arrays.asList(
            String.class,
            Character.class, Character.TYPE,
            Long.class, Long.TYPE,
            Integer.class, Integer.TYPE,
            Short.class, Short.TYPE,
            Byte.class, Byte.TYPE,
            Double.class, Double.TYPE,
            Float.class, Float.TYPE,
            Boolean.class, Boolean.TYPE
        ));
        for (val t : PRIMITIVES) {
            FORMATTERS.put(t, ValueTypes::convertPrimitive);
        }
        //shortcut
        FORMATTERS.put(NativeArray.class, ValueTypes::convertList);
        FORMATTERS.put(NativeObject.class, ValueTypes::convertScriptableObject);
        FORMATTERS.put(NativeFunction.class, ValueTypes::formatFunction);
        //general
        FORMATTERS.put(Map.class, ValueTypes::convertMap);
        FORMATTERS.put(List.class, ValueTypes::convertList);
        FORMATTERS.put(BaseFunction.class, ValueTypes::formatFunction);
        FORMATTERS.put(ArrowFunction.class, ValueTypes::formatFunction);
        FORMATTERS.put(Scriptable.class, ValueTypes::convertScriptableObject);
    }

    @Nullable
    public static BaseType convert(Object obj, TypeConverter converter, int limit) {
        if (obj == null) {
            return null;
        }
        val type = obj.getClass();
        val direct = FORMATTERS.get(type);
        if (direct != null) {
            return direct.convertOrDefault(obj, converter, limit);
        }
        for (val entry : FORMATTERS.entrySet()) {
            if (entry.getKey().isAssignableFrom(type)) {
                return entry.getValue().convertOrDefault(obj, converter, limit);
            }
        }
        return null;
    }

    public static JSPrimitiveType convertPrimitive(Object o, TypeConverter converter, int limit) {
        if (o == null || !PRIMITIVES.contains(o.getClass()) || limitConsumed(limit)) {
            return null;
        }
        return Types.primitive(ProbeJS.GSON.toJson(o));
    }

    public static BaseType convertOrDefault(Object obj, TypeConverter converter, int limit) {
        if (obj == null) {
            return Types.NULL;
        }
        val converted = convert(obj, converter, limit);
        if (converted != null) {
            return converted;
        }
        return converter.convertType(obj.getClass());
    }

    public static JSObjectType convertMap(Object obj, TypeConverter converter, int limit) {
        if (!(obj instanceof Map<?, ?> map) || limitConsumed(limit)) {
            return null;
        }
        val builder = Types.object();
        for (val entry : map.entrySet()) {
            val key = String.valueOf(entry.getKey());
            val value = entry.getValue();
            builder.member(key, convertOrDefault(value, converter, consumeLimit(limit)));
        }
        return builder.build();
    }

    public static BaseType convertList(Object obj, TypeConverter converter, int limit) {
        if (!(obj instanceof List<?> list) || limitConsumed(limit)) {
            return null;
        }

        val nextLimit = consumeLimit(limit);
        val converted = new BaseType[list.size()];
        for (int i = 0; i < list.size(); i++) {
            converted[i] = convertOrDefault(list.get(i), converter, nextLimit);
        }

        return Types.join(", ", "[", "]", converted);
    }

    public static BaseType convertScriptableObject(Object obj, TypeConverter converter, int limit) {
        if (!(obj instanceof ScriptableObject scriptable) || limitConsumed(limit)) {
            return null;// if not Scriptable, why call this
        }
        val nextLimit = consumeLimit(limit);
        val builder = Types.object();

        val prototype = scriptable.getPrototype();
        if (prototype.get("constructor", prototype) instanceof BaseFunction func) {
            //Resolves Object since they're not typed
            if (!func.getFunctionName().isEmpty() && !func.getFunctionName().equals("Object")) {
                return Types.primitive(func.getFunctionName());
            }
        }

        for (val id : scriptable.getIds()) {
            val value = id instanceof Number
                ? scriptable.get((Integer) id, scriptable)
                : scriptable.get((String) id, scriptable);
            builder.member(String.valueOf(id), convert(value, converter, nextLimit));
        }

        val proto = scriptable.getPrototype();
        for (val id : proto.getIds()) {
            val value = id instanceof Number
                ? proto.get((Integer) id, scriptable)
                : proto.get((String) id, scriptable);
            builder.member(String.valueOf(id), convert(value, converter, nextLimit));
        }

        return builder.build();
    }

    public static BaseType formatFunction(Object obj, TypeConverter converter, int limit) {
        if (!(obj instanceof BaseFunction fn) || limitConsumed(limit)) {
            return null;
        }
        val builder = Types.lambda().returnType(Types.ANY);
        val arity = fn.getArity();
        for (int i = 0; i < arity; i++) {
            builder.param("arg" + i, Types.ANY);
        }
        return builder.build();
    }

    private static int consumeLimit(int limit) {
        return limit < 0 ? -1 : limit - 1;
    }

    private static boolean limitConsumed(int limit) {
        return limit == 0;
    }

    interface ValueTypeConverter {
        BaseType convert(Object obj, TypeConverter converter, int depth);

        default BaseType convertOrDefault(Object object, TypeConverter converter, int depth) {
            if (object == null) {
                return Types.NULL;
            } else if (limitConsumed(depth)) {
                return converter.convertType(object.getClass());
            }
            val converted = this.convert(object, converter, depth);
            return converted == null ? converter.convertType(object.getClass()) : converted;
        }

        default BaseType convert(Object obj, TypeConverter converter) {
            return convert(obj, converter, -1);
        }
    }
}
