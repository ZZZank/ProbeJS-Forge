package zzzank.probejs.lang.transpiler;

import dev.latvian.kubejs.script.ScriptManager;
import dev.latvian.mods.rhino.annotations.typing.Generics;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import zzzank.probejs.features.rhizo.RhizoState;
import zzzank.probejs.lang.java.clazz.ClassPath;
import zzzank.probejs.lang.java.type.TypeAdapter;
import zzzank.probejs.lang.java.type.TypeDescriptor;
import zzzank.probejs.lang.java.type.impl.*;
import zzzank.probejs.lang.transpiler.redirect.TypeRedirect;
import zzzank.probejs.lang.typescript.code.type.*;
import zzzank.probejs.lang.typescript.code.type.js.JSJoinedType;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Adapts a TypeDescriptor into a BaseType
 */
public class TypeConverter {

    public final List<TypeRedirect> typeRedirects = new ArrayList<>();
    public final ScriptManager scriptManager;

    public TypeConverter(ScriptManager manager) {
        this.scriptManager = manager;
    }

    public void addTypeRedirect(TypeRedirect redirect) {
        typeRedirects.add(Objects.requireNonNull(redirect));
    }

    public BaseType convertType(TypeDescriptor descriptor) {
        for (val typeRedirect : typeRedirects) {
            if (typeRedirect.test(descriptor, this)) {
                return typeRedirect.apply(descriptor, this);
            }
        }
        return convertTypeBuiltin(descriptor);
    }

    public BaseType convertTypeExcluding(TypeDescriptor descriptor, TypeRedirect excludedRedirect) {
        for (val redirect : typeRedirects) {
            if (redirect != excludedRedirect && redirect.test(descriptor, this)) {
                return redirect.apply(descriptor, this);
            }
        }
        return convertTypeBuiltin(descriptor);
    }

    public @NotNull BaseType convertTypeBuiltin(TypeDescriptor descriptor) {
        if (descriptor instanceof ClassType classType) {
            return new TSClassType(classType.classPath);
        } else if (descriptor instanceof ArrayType arrayType) {
            return new TSArrayType(convertType(arrayType.component));
        } else if (descriptor instanceof ParamType paramType) {
            if (RhizoState.GENERIC_ANNOTATION) {
                val generics = paramType.getAnnotation(Generics.class);
                if (generics != null) {
                    val baseType = generics.base() == Object.class
                        ? convertType(paramType.base)
                        : new TSClassType(ClassPath.fromJava(generics.base()));
                    val params = Arrays
                        .stream(generics.value())
                        .map(c -> (BaseType) new TSClassType(ClassPath.fromJava(c)))
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
                case 0 -> Types.generic(variableType.symbol);
                case 1 -> Types.generic(variableType.symbol, convertType(desc.get(0)));
                default -> Types.generic(
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
