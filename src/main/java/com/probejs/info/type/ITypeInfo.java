package com.probejs.info.type;

public interface ITypeInfo {
    ITypeInfo getBaseType();

    Class<?> getResolvedClass();

    String getTypeName();

    String wrapTypeName(String rawName);

    ITypeInfo copy();

    boolean assignableFrom(ITypeInfo info);
}
