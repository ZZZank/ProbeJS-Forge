package com.probejs.info;

import com.probejs.info.MethodInfo.ParamInfo;
import com.probejs.info.type.ITypeInfo;
import com.probejs.info.type.TypeInfoParameterized;
import com.probejs.info.type.TypeInfoVariable;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
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
        if (!walkType) {
            return new HashSet<>(0);
        }
        Set<Class<?>> result = new HashSet<>();
        if (tInfo instanceof TypeInfoParameterized) {
            TypeInfoParameterized tInfoP = (TypeInfoParameterized) tInfo;
            result.add(tInfoP.getResolvedClass());
            result.addAll(walkTypes(tInfoP.getParamTypes()));
        } else if (tInfo instanceof TypeInfoVariable) {
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

    private Set<Class<?>> walkTypes(Collection<? extends ITypeInfo> tInfos) {
        Set<Class<?>> result = new HashSet<>();
        for (ITypeInfo tInfo : tInfos) {
            result.addAll(walkType(tInfo));
        }
        return result;
    }

    private Set<Class<?>> touch(Set<Class<?>> classes) {
        Set<Class<?>> result = new HashSet<>();
        for (Class<?> clazz : classes) {
            ClassInfo info = ClassInfo.ofCache(clazz);
            result.addAll(walkTypes(info.getParameters()));
            if (walkSuper) {
                ClassInfo superclass = info.getSuperClass();
                if (superclass != null) {
                    result.addAll(walkType(info.getSuperType()));
//                    result.add(superclass.getClazzRaw());
//                    result.addAll(walkTypes(superclass.getParameters()));
                for (ITypeInfo cInfo : info.getInterfaces()) {
                    result.addAll(walkType(cInfo));
                }
                }
                //TODO: not a good idea, we needs ClassInfo rewriting
                /*
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
                */
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
            for (ConstructorInfo cInfo : info.getConstructorInfos()) {
                for (ParamInfo pInfo : cInfo.getParams()) {
                    result.addAll(walkType(pInfo.getType()));
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
