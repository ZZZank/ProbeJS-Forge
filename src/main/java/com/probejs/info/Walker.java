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
    private final boolean walkMethod;
    private final boolean walkField;
    private final boolean walkSuper;
    private final boolean walkType;

    public Walker(
        Set<Class<?>> initial,
        boolean walkMethod,
        boolean walkField,
        boolean walkSuper,
        boolean walkType
    ) {
        this.initial = initial;
        this.walkMethod = walkMethod;
        this.walkField = walkField;
        this.walkSuper = walkSuper;
        this.walkType = walkType;
    }

    public Walker(Set<Class<?>> initial) {
        this(initial, true, true, true, true);
    }

    public Walker withWalkMethod(boolean walkMethod) {
        return new Walker(initial, walkMethod, walkField, walkSuper, walkType);
    }

    public Walker withWalkField(boolean walkField) {
        return new Walker(initial, walkMethod, walkField, walkSuper, walkType);
    }

    public Walker withWalkSuper(boolean walkSuper) {
        return new Walker(initial, walkMethod, walkField, walkSuper, walkType);
    }

    public Walker withWalkType(boolean walkType) {
        return new Walker(initial, walkMethod, walkField, walkSuper, walkType);
    }

    private Set<Class<?>> walkType(ITypeInfo tInfo) {
        Set<Class<?>> result = new HashSet<>();
        if (tInfo instanceof TypeInfoParameterized && walkType) {
            TypeInfoParameterized parType = (TypeInfoParameterized) tInfo;
            for (ITypeInfo info : parType.getParamTypes()) {
                result.addAll(walkType(info));
            }
        }
        if (tInfo instanceof TypeInfoVariable) {
            ((TypeInfoVariable) tInfo).getBounds()
                .stream()
                .map(ITypeInfo::getResolvedClass)
                .forEach(result::add);
        } else {
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
                for (FieldInfo fInfo : info.getFieldInfos()) {
                    result.addAll(walkType(fInfo.getType()));
                }
            }
            if (walkMethod) {
                for (MethodInfo mInfo : info.getMethodInfos()) {
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
