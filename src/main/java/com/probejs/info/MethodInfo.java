package com.probejs.info;

import com.probejs.info.type.ITypeInfo;
import com.probejs.info.type.TypeInfoResolver;
// import dev.latvian.mods.rhino.mod.util.RemappingHelper;
import dev.latvian.mods.rhino.util.HideFromJS;
// import dev.latvian.mods.rhino.util.Remapper;
// 1.16 doesn't have method remapper, so

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MethodInfo implements Comparable<MethodInfo> {

    private final String name;
    private final boolean shouldHide;
    private final int modifiers;
    private final Class<?> from;
    private ITypeInfo returnType;
    private List<ParamInfo> params;
    private List<ITypeInfo> typeVariables;

    // public static final Remapper RUNTIME = RemappingHelper.createModRemapper();

    private static String getRemappedOrDefault(Method method, Class<?> from) {
        // String s = RUNTIME.getMappedMethod(from, method);
        // return s.isEmpty() ? method.getName() : s;
        return method.getName();
    }

    public MethodInfo(Method method, Class<?> from) {
        this.name = getRemappedOrDefault(method, from);
        this.shouldHide = method.getAnnotation(HideFromJS.class) != null;
        this.from = from;
        this.modifiers = method.getModifiers();
        this.returnType = TypeInfoResolver.resolveType(method.getGenericReturnType());
        this.params = Arrays.stream(method.getParameters()).map(ParamInfo::new).collect(Collectors.toList());
        this.typeVariables =
            Arrays
                .stream(method.getTypeParameters())
                .map(TypeInfoResolver::resolveType)
                .collect(Collectors.toList());
    }

    public String getName() {
        return name;
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

    public ITypeInfo getReturnType() {
        return returnType;
    }

    public List<ParamInfo> getParams() {
        return params;
    }

    public List<ITypeInfo> getTypeVariables() {
        return typeVariables;
    }

    public ClassInfo getFrom() {
        return ClassInfo.ofCache(from);
    }

    public void setParams(List<ParamInfo> params) {
        this.params = params;
    }

    public void setReturnType(ITypeInfo returnType) {
        this.returnType = returnType;
    }

    public void setTypeVariables(List<ITypeInfo> typeVariables) {
        this.typeVariables = typeVariables;
    }

    public static class ParamInfo {

        private final String name;
        private ITypeInfo type;
        private final boolean isVarArgs;

        public ParamInfo(Parameter parameter) {
            this.name = parameter.getName();
            this.type = TypeInfoResolver.resolveType(parameter.getParameterizedType());
            this.isVarArgs = parameter.isVarArgs();
        }

        public String getName() {
            return name;
        }

        public ITypeInfo getType() {
            return type;
        }

        public boolean isVarArgs() {
            return isVarArgs;
        }

        public void setTypeInfo(ITypeInfo type) {
            this.type = type;
        }
    }

    @Override
    public int compareTo(MethodInfo o) {
        return o.name.compareTo(name);
    }
}
