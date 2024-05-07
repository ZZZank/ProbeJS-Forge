package com.probejs.info.type;

import lombok.Data;

import java.lang.reflect.Type;

@Data
public class TypeLiteral implements IType {

    private final String value;

    @Override
    public Type getRaw() {
        return null;
    }

    @Override
    public IType getBase() {
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
