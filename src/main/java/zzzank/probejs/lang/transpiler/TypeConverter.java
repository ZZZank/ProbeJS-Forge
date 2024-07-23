package zzzank.probejs.lang.transpiler;

import dev.latvian.kubejs.script.ScriptManager;
import dev.latvian.mods.rhino.annotations.typing.Generics;
import lombok.val;
import zzzank.probejs.features.rhizo.RhizoState;
import zzzank.probejs.lang.java.clazz.ClassPath;
import zzzank.probejs.lang.java.type.TypeAdapter;
import zzzank.probejs.lang.java.type.TypeDescriptor;
import zzzank.probejs.lang.java.type.impl.*;
import zzzank.probejs.lang.typescript.code.type.*;
import zzzank.probejs.lang.typescript.code.type.js.JSJoinedType;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Adapts a TypeDescriptor into a BaseType
 */
public class TypeConverter {

    public final Map<ClassPath, BaseType> predefinedTypes = new HashMap<>();
    public final ScriptManager scriptManager;

    public TypeConverter(ScriptManager manager) {
        this.scriptManager = manager;
    }

    public void addType(Class<?> clazz, BaseType type) {
        predefinedTypes.put(new ClassPath(clazz), type);
    }

    public BaseType convertType(TypeDescriptor descriptor) {
        if (descriptor instanceof ClassType classType) {
            return predefinedTypes.getOrDefault(classType.classPath, new TSClassType(classType.classPath));
        } else if (descriptor instanceof ArrayType arrayType) {
            return new TSArrayType(convertType(arrayType.component));
        } else if (descriptor instanceof ParamType paramType) {
            if (RhizoState.GENERIC_ANNOTATION) {
                val generics = paramType.getAnnotation(Generics.class);
                if (generics != null) {
                    val baseType = generics.base() == Object.class
                        ? convertType(paramType.base)
                        : new TSClassType(new ClassPath(generics.base()));
                    val params = Arrays
                        .stream(generics.value())
                        .map(c -> (BaseType) new TSClassType(new ClassPath(c)))
                        .collect(Collectors.toList());
                    return new TSParamType(baseType, params);
                }
            }

            BaseType base = convertType(paramType.base);
            if (base == Types.ANY) {
                return Types.ANY;
            }
            List<BaseType> params = paramType.params.stream().map(this::convertType).collect(Collectors.toList());
            return new TSParamType(base, params);
        } else if (descriptor instanceof VariableType variableType) {
            List<TypeDescriptor> desc = variableType.descriptors;
            return switch (desc.size()) {
                case 0 -> new TSVariableType(variableType.symbol, null);
                case 1 -> new TSVariableType(variableType.symbol, convertType(desc.get(0)));
                default -> new TSVariableType(
                    variableType.symbol,
                    new JSJoinedType.Intersection(desc.stream().map(this::convertType).collect(Collectors.toList()))
                );
            };
        } else if (descriptor instanceof WildType wildType) {
            return wildType.stream().findAny().map(this::convertType).orElse(Types.ANY);
        }
        throw new RuntimeException("Unknown subclass of TypeDescriptor.");
    }

    public BaseType convertType(Type javaType) {
        return convertType(TypeAdapter.getTypeDescription(javaType));
    }
}
