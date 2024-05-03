package com.probejs.info.type.ts;

import com.probejs.info.type.ITypeInfo;

import java.lang.reflect.Type;

public class TypeLiteral implements ITypeInfo {

    private final String value;

    public TypeLiteral(String value) {
        this.value = value;
    }

    @Override
    public Type getRaw() {
        return null;
    }

    @Override
    public ITypeInfo getBaseType() {
        return this;
    }

    @Override
    public Class<?> getResolvedClass() {
        return null;
    }

    @Override
    public String getTypeName() {
        return this.value;
    }

    @Override
    public ITypeInfo copy() {
        return this;
    }

    @Override
    public boolean assignableFrom(ITypeInfo info) {
        return false;
    }
}
