package com.probejs.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public interface DummyIRemapper {

    String getMappedClass(Class<?> from);

    String getUnmappedClass(String from);

    String getMappedField(Class<?> from, Field field);

    String getMappedMethod(Class<?> from, Method method);

}

