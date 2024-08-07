package com.probejs.info.clazz;

import com.probejs.info.type.JavaType;
import com.probejs.info.type.TypeResolver;
import com.probejs.integration.RemapperBridge;
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
    private final ClassInfo from;
    @Setter
    private List<ParamInfo> params;
    @Setter
    private List<JavaType> typeVariables;

    public MethodInfo(Method method, Class<?> from) {
        super(RemapperBridge.remapMethod(from, method), TypeResolver.resolve(method.getGenericReturnType()));
        this.raw = method;
        this.shouldHide = method.getAnnotation(HideFromJS.class) != null;
        this.from = ClassInfo.ofCache(from);
        this.modifiers = method.getModifiers();
        this.params = Arrays.stream(method.getParameters()).map(ParamInfo::new).collect(Collectors.toList());
        this.typeVariables = Arrays.stream(method.getTypeParameters())
            .map(TypeResolver::resolve)
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
            super(parameter.getName(), TypeResolver.resolve(parameter.getParameterizedType()));
            this.isVarArgs = parameter.isVarArgs();
        }
    }
}
