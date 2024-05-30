package com.probejs.rewrite.doc.type;

import com.probejs.rewrite.doc.type.java.JavaType;
import com.probejs.rewrite.doc.type.java.TypeArray;
import com.probejs.rewrite.doc.type.java.TypeClazz;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public abstract class DocTypeResolver {

    private static final Map<Class<? extends Type>, Function<Type, JavaType>> JAVA;

    static {
        JAVA = new HashMap<>();
        JAVA.put(Class.class, TypeClazz::new);
        JAVA.put(GenericArrayType.class, TypeArray::new);
    }

    public static JavaType ofJava(Type type) {
        return JAVA.get(type.getClass()).apply(type);
    }
}
