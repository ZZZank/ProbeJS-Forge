package com.probejs.info.type;

import javax.annotation.Nullable;
import java.lang.reflect.Type;

public interface ITypeInfo {
    @Nullable
    Type getRaw();

    ITypeInfo getBaseType();

    @Nullable
    Class<?> getResolvedClass();

    String getTypeName();

    ITypeInfo copy();

    boolean assignableFrom(ITypeInfo info);
}
