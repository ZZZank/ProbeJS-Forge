package com.probejs.formatter.resolver;

import com.probejs.document.DocManager;
import com.probejs.document.type.TypeRaw;
import com.probejs.formatter.FormatterClass;
import com.probejs.formatter.FormatterType;
import com.probejs.info.SpecialData;
import com.probejs.info.type.ITypeInfo;
import com.probejs.info.type.TypeInfoClass;
import com.probejs.info.type.TypeInfoParameterized;
import dev.latvian.mods.rhino.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SpecialTypes {

    public static final Set<Class<?>> skippedSpecials = new HashSet<>();

    public static String formatClassLike(ITypeInfo obj) {
        ITypeInfo inner = null;
        if (obj instanceof TypeInfoParameterized) {
            TypeInfoParameterized cls = (TypeInfoParameterized) obj;
            inner = cls.getParamTypes().get(0);
        } else if (obj instanceof TypeInfoClass) {
            TypeInfoClass cls = (TypeInfoClass) obj;
            inner = cls;
        }
        if (inner == null) {
            return "any";
        }
        return String.format("typeof %s", FormatterType.of(inner.getBaseType(), false).format());
    }

    private static String formatValueOrType(Object obj) {
        String formattedValue = NameResolver.formatValue(obj);
        if (formattedValue == null) {
            if (
                !NameResolver.resolvedNames.containsKey(obj.getClass().getName()) &&
                !obj.getClass().getName().contains("$Lambda")
            ) {
                NameResolver.resolveName(obj.getClass());
            }
            formattedValue = FormatterClass.formatParameterized(new TypeInfoClass(obj.getClass()));
        }
        return formattedValue;
    }

    public static String formatMap(Object obj) {
        if (!(obj instanceof Map<?, ?>)) {
            return "{}";
        }
        List<String> values = new ArrayList<>();
        Map<?, ?> map = (Map<?, ?>) obj;
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            Object key = entry.getKey();
            Object value = entry.getValue();
            String formattedKey = NameResolver.formatValue(key);
            if (formattedKey == null) {
                continue;
            }
            String formattedValue = formatValueOrType(value);
            values.add(String.format("%s:%s", formattedKey, formattedValue));
        }
        return String.format("{%s}", String.join(",", values));
    }

    public static String formatList(Object obj) {
        if (!(obj instanceof List<?>)) {
            return "[]";
        }
        List<String> values = new ArrayList<>();
        List<?> list = (List<?>) obj;
        for (Object o : list) {
            String formattedValue = NameResolver.formatValue(o);
            if (formattedValue == null) formattedValue = "undefined";
            values.add(formattedValue);
        }
        return String.format("[%s]", String.join(", ", values));
    }

    public static String formatScriptable(Object obj) {
        if (!(obj instanceof ScriptableObject)) {
            // if not Scriptable, why call this
            return "{}";
        }
        List<String> values = new ArrayList<>();
        ScriptableObject scriptable = (ScriptableObject) obj;

        Scriptable prototype = scriptable.getPrototype();
        if (prototype.get("constructor", prototype) instanceof BaseFunction) {
            BaseFunction func = (BaseFunction) prototype.get("constructor", prototype);
            //Resolves Object since they're not typed
            if (!func.getFunctionName().isEmpty() && !func.getFunctionName().equals("Object")) {
                return func.getFunctionName();
            }
        }

        for (Object id : scriptable.getIds()) {
            String formattedKey = NameResolver.formatValue(id);
            Object value;
            if (id instanceof Number) {
                value = scriptable.get((Integer) id, scriptable);
            } else {
                value = scriptable.get((String) id, scriptable);
            }
            String formattedValue = formatValueOrType(value);
            values.add(String.format("%s:%s", formattedKey, formattedValue));
        }

        Scriptable proto = scriptable.getPrototype();
        for (Object id : proto.getIds()) {
            String formattedKey = NameResolver.formatValue(id);
            Object value;
            if (id instanceof Number) {
                value = proto.get((Integer) id, scriptable);
            } else {
                value = proto.get((String) id, scriptable);
            }
            String formattedValue = formatValueOrType(value);
            values.add(String.format("%s:%s", formattedKey, formattedValue));
        }
        return String.format("{%s}", String.join(",", values));
    }

    public static String formatFunction(Object obj) {
        if (!(obj instanceof BaseFunction)) {
            return null;
        }
        BaseFunction function = (BaseFunction) obj;
        return String.format(
            "(%s) => any",
            IntStream
                .range(0, function.getLength())
                .mapToObj(i -> "arg" + i)
                .collect(Collectors.joining(", "))
        );
    }

    public static String formatNJO(Object obj) {
        if (!(obj instanceof NativeJavaObject)) {
            return null;
        }
        NativeJavaObject njo = (NativeJavaObject) obj;
        return formatValueOrType(njo.unwrap());
    }

    public static void processSpecialAssignments() {
        //specialClassAssigner
        NameResolver.specialClassAssigner.forEach((clazzName, assignProvider) -> {
            String name = clazzName.getName();
            for (String assignTo : assignProvider.get()) {
                DocManager.addAssignable(name, new TypeRaw(assignTo));
            }
        });
        //registry
        SpecialData
            .computeRegistryInfos()
            .stream()
            .filter(info -> !info.names.isEmpty())
            .forEach(info -> {
                Class<?> registrySuperType = info.forgeRaw.getRegistrySuperType();
                String name = String.format(
                    "Registry.%s.%s",
                    info.id.getNamespace(),
                    info.id.getPath().replace('/', '$')
                );
                DocManager.addAssignable(registrySuperType.getName(), new TypeRaw(name));
            });
    }
}
