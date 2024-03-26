package com.probejs.info;

import com.probejs.info.MethodInfo.ParamInfo;
import com.probejs.info.type.ITypeInfo;
import com.probejs.info.type.TypeInfoParameterized;
import com.probejs.info.type.TypeInfoVariable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class Walker {

    private final Set<Class<?>> initial;
    private boolean walkMethod = true;
    private boolean walkField = true;
    private boolean walkSuper = true;
    private boolean walkType = true;

    public Walker(Set<Class<?>> initial) {
        this.initial = initial;
    }

    public void setWalkField(boolean walkField) {
        this.walkField = walkField;
    }

    public void setWalkMethod(boolean walkMethod) {
        this.walkMethod = walkMethod;
    }

    public void setWalkSuper(boolean walkSuper) {
        this.walkSuper = walkSuper;
    }

    public void setWalkType(boolean walkType) {
        this.walkType = walkType;
    }

    private Set<Class<?>> walkType(ITypeInfo tInfo) {
        Set<Class<?>> result = new HashSet<>();
        if (tInfo instanceof TypeInfoParameterized && walkType) {
            TypeInfoParameterized parType = (TypeInfoParameterized) tInfo;
            for (ITypeInfo info : parType.getParamTypes()) {
                result.addAll(walkType(info));
            }
        }
        if (!(tInfo instanceof TypeInfoVariable)) {
            result.add(tInfo.getResolvedClass());
        }
        result.removeIf(Objects::isNull);
        return result;
    }

    private Set<Class<?>> touch(Set<Class<?>> classes) {
        Set<Class<?>> result = new HashSet<>();
        for (Class<?> clazz : classes) {
            ClassInfo info = ClassInfo.ofCache(clazz);

            if (walkSuper) {
                ClassInfo superclass = info.getSuperClass();
                if (superclass != null) {
                    result.add(superclass.getClazzRaw());
                }
                for (ClassInfo cInfo : info.getInterfaces()) {
                    result.add(cInfo.getClazzRaw());
                }
            }
            if (walkField) {
                for (FieldInfo fInfo : info.getFieldInfo()) {
                    result.addAll(walkType(fInfo.getType()));
                }
            }
            if (walkMethod) {
                for (MethodInfo mInfo : info.getMethodInfo()) {
                    result.addAll(walkType(mInfo.getReturnType()));
                    for (ParamInfo pInfo : mInfo.getParams()) {
                        result.addAll(walkType(pInfo.getType()));
                    }
                }
            }
        }
        result.removeIf(Objects::isNull);
        return result;
    }

    public Set<Class<?>> walk() {
        Set<Class<?>> result = new HashSet<>(initial);
        Set<Class<?>> current = touch(result);

        while (!current.isEmpty()) {
            result.addAll(current);
            current = touch(current).stream().filter(c -> !result.contains(c)).collect(Collectors.toSet());
        }
        return result;
    }
}
