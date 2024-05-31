package com.probejs.info.type;

import java.lang.reflect.Type;

public interface JavaType {

    Type getRaw();

    JavaType getBase();

    Class<?> getResolvedClass();

    String getTypeName();

    JavaType copy();

    boolean assignableFrom(JavaType info);
}
