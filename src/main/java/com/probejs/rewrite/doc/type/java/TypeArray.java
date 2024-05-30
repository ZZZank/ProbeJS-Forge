package com.probejs.rewrite.doc.type.java;

import com.probejs.rewrite.doc.type.DocTypeResolver;
import lombok.Getter;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.util.Collection;

@Getter
public class TypeArray implements JavaType {

    private final JavaType base;
    private final boolean isGenericArray;

    /**
     * @return {@code type instanceof GenericArrayType}
     */
    public static boolean isGenericArray(Type type) {
        return type instanceof GenericArrayType;
    }

    /**
     * @return true if it can be cast into {@code Class<?>} and {@code ((Class<?>) type).isArray()}
     * is true, otherwise false
     */
    public static boolean isClassArray(Type type) {
        if (type instanceof Class<?> clazz) {
            return clazz.isArray();
        }
        return false;
    }

    public static boolean test(Type type) {
        return isGenericArray(type) || isClassArray(type);
    }

    public TypeArray(Type type) {
        if (isGenericArray(type)) {
            this.base = DocTypeResolver.ofJava(((GenericArrayType) type).getGenericComponentType());
            isGenericArray = true;
        } else if (isClassArray(type)) {
            assert type instanceof Class<?>;
            this.base = DocTypeResolver.ofJava(((Class<?>) type).getComponentType());
            isGenericArray = false;
        } else {
            throw new IllegalArgumentException("Argument 'type' is not array");
        }
    }

    @Override
    public Type raw() {
        return base.raw();
    }

    @Override
    public JavaType base() {
        return base;
    }

    @Override
    public Collection<Class<?>> relatedClasses() {
        return base.relatedClasses();
    }
}
