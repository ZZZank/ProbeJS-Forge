package com.probejs.info;

import com.probejs.info.MethodInfo.ParamInfo;
import com.probejs.info.type.ITypeInfo;
import com.probejs.info.type.TypeInfoParameterized;
import com.probejs.info.type.TypeInfoVariable;

import java.util.*;
import java.util.stream.Collectors;

public class Walker {

    private final Set<Class<?>> initial;

    public Walker(Set<Class<?>> initial) {
        this.initial = initial;
    }

    private Set<Class<?>> walkType(ITypeInfo tInfo) {
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
            result.addAll(walkTypes(info.getTypeParameters()));
            //super
            ClassInfo superclass = info.getSuperClass();
            if (superclass != null) {
                result.addAll(walkType(info.getSuperType()));
                result.addAll(walkTypes(superclass.getTypeParameters()));
            }
            result.addAll(walkTypes(info.getInterfaces()));
            //field
            for (FieldInfo fInfo : info.getFieldInfos()) {
                result.addAll(walkType(fInfo.getType()));
            }
            //method
            for (MethodInfo mInfo : info.getMethodInfos()) {
                result.addAll(walkType(mInfo.getReturnType()));
                for (ParamInfo pInfo : mInfo.getParams()) {
                    result.addAll(walkType(pInfo.getType()));
                }
            }
            //constructor
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
