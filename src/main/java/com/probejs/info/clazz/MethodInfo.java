package com.probejs.info.clazz;

import com.probejs.info.type.IType;
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

@Getter
public class MethodInfo extends BaseMemberInfo {

    private final Method raw;
    private final boolean shouldHide;
    private final int modifiers;
    /**
     * the classInfo that the method belongs to is NOT in info cache when MethodInfo is being constructed
     */
    private final Class<?> from;
    @Setter
    private List<ParamInfo> params;
    @Setter
    private List<IType> typeVariables;

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
        super(getRemappedOrDefault(method, from), TypeResolver.resolveType(method.getGenericReturnType()));
        this.raw = method;
        this.shouldHide = method.getAnnotation(HideFromJS.class) != null;
        this.from = from;
        this.modifiers = method.getModifiers();
        this.params = Arrays.stream(method.getParameters()).map(ParamInfo::new).collect(Collectors.toList());
        this.typeVariables = Arrays.stream(method.getTypeParameters())
            .map(TypeResolver::resolveType)
            .collect(Collectors.toList());
    }

    public boolean isStatic() {
        return Modifier.isStatic(modifiers);
    }

    public boolean isAbstract() {
        return Modifier.isAbstract(modifiers);
    }

    @Getter
    public static class ParamInfo extends BaseMemberInfo {

        private final boolean isVarArgs;

        public ParamInfo(Parameter parameter) {
            this.name = parameter.getName();
            this.type = TypeResolver.resolveType(parameter.getParameterizedType());
            this.isVarArgs = parameter.isVarArgs();
        }
    }
}
