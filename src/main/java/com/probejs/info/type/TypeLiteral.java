package com.probejs.info.type;

import lombok.Data;

import java.lang.reflect.Type;

@Data
public class TypeLiteral implements JavaType {

    private final String value;

    @Override
    public Type getRaw() {
        return null;
    }

    @Override
    public JavaType getBase() {
        return this;
    }

    @Override
    public Class<?> getResolvedClass() {
        return String.class;
    }

    @Override
    public String getTypeName() {
        return this.value;
    }

    @Override
    public JavaType copy() {
        return this;
    }

    @Override
    public boolean assignableFrom(JavaType info) {
        if (!(info instanceof JavaTypeClass clazz)) {
            return false;
        }
        return clazz.getRaw().isAssignableFrom(String.class);
    }
}
