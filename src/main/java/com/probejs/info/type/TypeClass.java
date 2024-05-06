package com.probejs.info.type;

import com.probejs.info.clazz.ClassInfo;

import java.lang.reflect.Type;
import java.util.List;

public class TypeClass implements IType {

    public static boolean test(Type type) {
        return type instanceof Class<?>;
    }

    private final Class<?> raw;

    public TypeClass(Type type) {
        this.raw = (Class<?>) type;
    }

    private TypeClass(Class<?> type) {
        this.raw = type;
    }

    @Override
    public IType getBaseType() {
        return this;
    }

    @Override
    public Class<?> getResolvedClass() {
        return raw;
    }

    @Override
    public String getTypeName() {
        return this.raw.getTypeName();
    }

    @Override
    public IType copy() {
        return new TypeClass(raw);
    }

    @Override
    public boolean assignableFrom(IType info) {
        if (!(info instanceof TypeClass)) {
            return false;
        }
        TypeClass clazz = (TypeClass) info;
        return clazz.raw.isAssignableFrom(raw);
    }

    public List<TypeVariable> getTypeVariables() {
        return ClassInfo.ofCache(this.raw).getTypeParameters();
    }

    @Override
    public Type getRaw() {
        return this.raw;
    }
}
