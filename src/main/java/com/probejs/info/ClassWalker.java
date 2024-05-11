package com.probejs.info;

import com.probejs.info.clazz.*;
import com.probejs.info.type.*;
import com.probejs.info.type.IType;
import com.probejs.info.type.TypeVariable;
import lombok.val;

import java.util.*;
import java.util.stream.Collectors;

public class ClassWalker {

    private final Set<Class<?>> initial;

    public ClassWalker(Collection<Class<?>> initial) {
        this.initial = new HashSet<>(initial);
    }

    private Set<Class<?>> walkType(IType tInfo) {
        Set<Class<?>> result = new HashSet<>();
        if (tInfo instanceof TypeParameterized) {
            TypeParameterized tInfoP = (TypeParameterized) tInfo;
            result.add(tInfoP.getResolvedClass());
            result.addAll(walkTypes(tInfoP.getParamTypes()));
        } else if (tInfo instanceof TypeVariable) {
            ((TypeVariable) tInfo).getBounds()
                .stream()
                .map(IType::getResolvedClass)
                .forEach(result::add);
        } else if (tInfo instanceof TypeWildcard){
            TypeWildcard wInfo = (TypeWildcard) tInfo;
            result.add(tInfo.getResolvedClass());
//            result.addAll(walkTypes(wInfo.getLowerBounds()));
            result.addAll(walkTypes((wInfo.getUpperBounds())));
        } else {
            result.add(tInfo.getResolvedClass());
        }
        result.removeIf(Objects::isNull);
        return result;
    }

    private Set<Class<?>> walkTypes(Collection<? extends IType> tInfos) {
        Set<Class<?>> result = new HashSet<>();
        for (val tInfo : tInfos) {
            result.addAll(walkType(tInfo));
        }
        return result;
    }

    private Set<Class<?>> touch(Set<Class<?>> classes) {
        Set<Class<?>> result = new HashSet<>();
        for (Class<?> clazz : classes) {
            val info = ClassInfo.ofCache(clazz);
            //self
            result.addAll(walkTypes(info.getTypeParameters()));
            //super
            val superclass = info.getSuperClass();
            if (superclass != null) {
                result.addAll(walkType(info.getSuperType()));
                result.addAll(walkTypes(superclass.getTypeParameters()));
            }
            result.addAll(walkTypes(info.getInterfaces()));
            //field
            for (val fInfo : info.getFields()) {
                result.addAll(walkType(fInfo.getType()));
            }
            //method
            for (val mInfo : info.getMethods()) {
                result.addAll(walkType(mInfo.getType()));
                for (val pInfo : mInfo.getParams()) {
                    result.addAll(walkType(pInfo.getType()));
                }
            }
            //constructor
            for (val cInfo : info.getConstructors()) {
                for (val pInfo : cInfo.getParams()) {
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
            current = touch(current).parallelStream().filter(c -> !result.contains(c)).collect(Collectors.toSet());
        }
        return result;
    }
}
