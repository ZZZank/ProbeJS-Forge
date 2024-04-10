package com.probejs.info;

import com.probejs.info.MethodInfo.ParamInfo;
import com.probejs.info.type.ITypeInfo;
import com.probejs.info.type.TypeInfoParameterized;
import com.probejs.info.type.TypeInfoVariable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
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
    private final boolean walkSuperGenerics;

    public Walker(
        Set<Class<?>> initial,
        boolean walkMethod,
        boolean walkField,
        boolean walkSuper,
        boolean walkType,
        boolean walkSuperGenerics
    ) {
        this.initial = initial;
        this.walkMethod = walkMethod;
        this.walkField = walkField;
        this.walkSuper = walkSuper;
        this.walkType = walkType;
        this.walkSuperGenerics = walkSuperGenerics;
    }

    public Walker(Set<Class<?>> initial) {
        this(initial, true, true, true, true, true);
    }

    public Walker withWalkMethod(boolean walkMethod) {
        return new Walker(initial, walkMethod, walkField, walkSuper, walkType, walkSuperGenerics);
    }

    public Walker withWalkField(boolean walkField) {
        return new Walker(initial, walkMethod, walkField, walkSuper, walkType, walkSuperGenerics);
    }

    public Walker withWalkSuper(boolean walkSuper) {
        return new Walker(initial, walkMethod, walkField, walkSuper, walkType, walkSuperGenerics);
    }

    public Walker withWalkType(boolean walkType) {
        return new Walker(initial, walkMethod, walkField, walkSuper, walkType, walkSuperGenerics);
    }

    public Walker withWalkSuperGenerics(boolean walkSuperGenerics) {
        return new Walker(initial, walkMethod, walkField, walkSuper, walkType, walkSuperGenerics);
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

            if (this.walkSuperGenerics) {
                Type genericSuper = info.getClazzRaw().getGenericSuperclass();
                if (genericSuper instanceof ParameterizedType) {
                    Arrays
                        .stream(((ParameterizedType) genericSuper).getActualTypeArguments())
                        .filter(t -> t instanceof Class)
                        .map(t -> (Class<?>) t)
                        .forEach(result::add);
                }
                Arrays
                    .stream(info.getClazzRaw().getGenericInterfaces())
                    .filter(t -> t instanceof ParameterizedType)
                    .map(t -> (ParameterizedType) t)
                    .map(ParameterizedType::getActualTypeArguments)
                    .flatMap(Arrays::stream)
                    .filter(t -> t instanceof Class)
                    .map(t -> (Class<?>) t)
                    .forEach(result::add);
            }

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
