package com.probejs.info.type;

import java.lang.reflect.Type;

public class TypeLiteral implements IType {

    private final String value;

    public TypeLiteral(String value) {
        this.value = value;
    }

    @Override
    public Type getRaw() {
        return null;
    }

    @Override
    public IType getBaseType() {
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
    public IType copy() {
        return this;
    }

    @Override
    public boolean assignableFrom(IType info) {
        return false;
    }
}
