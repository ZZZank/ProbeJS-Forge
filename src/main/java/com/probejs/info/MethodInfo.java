package com.probejs.info;

import com.probejs.info.type.ITypeInfo;
import com.probejs.info.type.TypeResolver;
import com.probejs.util.RemapperBridge;
import dev.latvian.mods.rhino.util.HideFromJS;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MethodInfo {

    @Getter
    private final Method raw;
    @Getter
    private final String name;
    private final boolean shouldHide;
    private final int modifiers;
    private final Class<?> from;
    @Setter
    @Getter
    private ITypeInfo returnType;
    @Setter
    @Getter
    private List<ParamInfo> params;
    @Setter
    @Getter
    private List<ITypeInfo> typeVariables;

    private static String getRemappedOrDefault(Method method, Class<?> from) {
        String mapped = RemapperBridge.getRemapper().getMappedMethod(from, method);
        if (!mapped.isEmpty()) {
            return mapped;
        }
        // String s = REMAPPER.getMappedMethod(from, method);
        // return s.isEmpty() ? method.getName() : s;
        return method.getName();
    }

    public MethodInfo(Method method, Class<?> from) {
        this.raw = method;
        this.name = getRemappedOrDefault(method, from);
        this.shouldHide = method.getAnnotation(HideFromJS.class) != null;
        this.from = from;
        this.modifiers = method.getModifiers();
        this.returnType = TypeResolver.resolveType(method.getGenericReturnType());
        this.params = Arrays.stream(method.getParameters()).map(ParamInfo::new).collect(Collectors.toList());
        this.typeVariables =
            Arrays
                .stream(method.getTypeParameters())
                .map(TypeResolver::resolveType)
                .collect(Collectors.toList());
    }

    public boolean shouldHide() {
        return shouldHide;
    }

    public boolean isStatic() {
        return Modifier.isStatic(modifiers);
    }

    public boolean isAbstract() {
        return Modifier.isAbstract(modifiers);
    }

    public ClassInfo getFrom() {
        return ClassInfo.ofCache(from);
    }

    @Getter
    public static class ParamInfo {

        private final String name;
        @Setter
        private ITypeInfo type;
        private final boolean isVarArgs;

        public ParamInfo(Parameter parameter) {
            this.name = parameter.getName();
            this.type = TypeResolver.resolveType(parameter.getParameterizedType());
            this.isVarArgs = parameter.isVarArgs();
        }
    }
}
