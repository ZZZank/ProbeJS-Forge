package com.probejs.info.type;

import java.lang.reflect.Type;

public interface ITypeInfo {
    Type getRaw();

    ITypeInfo getBaseType();

    Class<?> getResolvedClass();

    String getTypeName();

    ITypeInfo copy();

    boolean assignableFrom(ITypeInfo info);
}
