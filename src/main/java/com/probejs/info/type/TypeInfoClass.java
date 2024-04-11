package com.probejs.info.type;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TypeInfoClass implements ITypeInfo {

    public static boolean test(Type type) {
        return type instanceof Class<?>;
    }

    private final Class<?> raw;

    public TypeInfoClass(Type type) {
        this.raw = (Class<?>) type;
    }

    private TypeInfoClass(Class<?> type) {
        this.raw = type;
    }

    @Override
    public ITypeInfo getBaseType() {
        return this;
    }

    @Override
    public Class<?> getResolvedClass() {
        return raw;
    }

    @Override
    public String getTypeName() {
        return wrapTypeName(this.raw.getTypeName());
    }

    @Override
    public String wrapTypeName(String rawName) {
        return rawName;
    }

    @Override
    public ITypeInfo copy() {
        return new TypeInfoClass(raw);
    }

    @Override
    public boolean assignableFrom(ITypeInfo info) {
        if (!(info instanceof TypeInfoClass)) {
            return false;
        }
        TypeInfoClass clazz = (TypeInfoClass) info;
        return clazz.raw.isAssignableFrom(raw);
    }

    public List<ITypeInfo> getTypeVariables() {
        return Arrays
            .stream(raw.getTypeParameters())
            .map(TypeResolver::resolveType)
            .collect(Collectors.toList());
    }

    @Override
    public Type getRaw() {
        return this.raw;
    }
}
