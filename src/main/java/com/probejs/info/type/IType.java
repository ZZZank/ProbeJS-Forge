package com.probejs.info.type;

import javax.annotation.Nullable;
import java.lang.reflect.Type;

public interface IType {
    @Nullable
    Type getRaw();

    IType getBaseType();

    @Nullable
    Class<?> getResolvedClass();

    String getTypeName();

    IType copy();

    boolean assignableFrom(IType info);
}
