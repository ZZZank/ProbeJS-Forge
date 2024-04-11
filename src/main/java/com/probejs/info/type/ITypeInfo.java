package com.probejs.info.type;

import java.lang.reflect.Type;

public interface ITypeInfo {
    Type getRaw();

    ITypeInfo getBaseType();

    Class<?> getResolvedClass();

    String getTypeName();

    String wrapTypeName(String rawName);

    ITypeInfo copy();

    boolean assignableFrom(ITypeInfo info);
}
