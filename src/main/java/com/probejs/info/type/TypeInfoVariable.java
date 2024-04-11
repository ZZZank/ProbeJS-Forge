package com.probejs.info.type;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TypeInfoVariable implements ITypeInfo {

    public static boolean test(Type type) {
        return type instanceof TypeVariable;
    }

    private final TypeVariable<?> raw;
    private boolean underscored = false;

    public TypeInfoVariable(Type type) {
        this.raw = (TypeVariable<?>) type;
    }

    private TypeInfoVariable(TypeVariable<?> inner) {
        this.raw = inner;
    }

    public void setUnderscored(boolean underscored) {
        this.underscored = underscored;
    }

    @Override
    public TypeVariable<?> getRaw() {
        return this.raw;
    }

    @Override
    public ITypeInfo getBaseType() {
        return this;
    }

    @Override
    public String getTypeName() {
        return wrapTypeName(this.raw.getTypeName());
    }

    @Override
    public String wrapTypeName(String rawName) {
        if (underscored) {
            return rawName + '_';
        }
        return rawName;
    }

    @Override
    public ITypeInfo copy() {
        TypeInfoVariable copied = new TypeInfoVariable(raw);
        copied.setUnderscored(underscored);
        return copied;
    }

    @Override
    public boolean assignableFrom(ITypeInfo info) {
        return info instanceof TypeInfoVariable;
    }

    @Override
    public Class<?> getResolvedClass() {
        return Object.class;
    }

    public List<ITypeInfo> getBounds() {
        return Arrays
            .stream(this.raw.getBounds())
            .map(TypeResolver::resolveType)
            .collect(Collectors.toList());
    }
}
