package com.probejs.info.type;

import java.lang.reflect.Type;

public interface IType {

    Type getRaw();

    IType getBase();

    Class<?> getResolvedClass();

    String getTypeName();

    IType copy();

    boolean assignableFrom(IType info);
}
