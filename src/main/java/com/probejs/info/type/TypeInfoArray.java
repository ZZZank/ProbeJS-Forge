package com.probejs.info.type;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.util.List;

public class TypeInfoArray implements ITypeInfo {

    /**
     * @return {@code type instanceof GenericArrayType}
     */
    public static boolean isGenericArray(Type type) {
        return type instanceof GenericArrayType;
    }

    /**
     * @return true if it can be casted into {@code Class<?>} and {@code ((Class<?>) type).isArray()}
     * is true, otherwise false
     */
    public static boolean isClassArray(Type type) {
        if (type instanceof Class<?>) {
            Class<?> clazz = (Class<?>) type;
            return clazz.isArray();
        }
        return false;
    }

    public static boolean test(Type type) {
        return isGenericArray(type) || isClassArray(type);
    }

    private ITypeInfo type;

    public TypeInfoArray(Type type) {
        if (isGenericArray(type)) {
            this.type = InfoTypeResolver.resolveType(((GenericArrayType) type).getGenericComponentType());
        }
        if (isClassArray(type)) {
            assert type instanceof Class<?>;
            this.type = InfoTypeResolver.resolveType(((Class<?>) type).getComponentType());
        }
    }

    private TypeInfoArray(ITypeInfo inner) {
        this.type = inner;
    }

    @Override
    public ITypeInfo getBaseType() {
        return type;
    }

    @Override
    public Class<?> getResolvedClass() {
        return List.class;
    }

    @Override
    public String getTypeName() {
        return wrapTypeName(this.type.getTypeName());
    }

    @Override
    public String wrapTypeName(String rawName) {
        return rawName + "[]";
    }

    public void setType(ITypeInfo type) {
        this.type = type;
    }

    @Override
    public ITypeInfo copy() {
        return new TypeInfoArray(type.copy());
    }

    @Override
    public boolean assignableFrom(ITypeInfo info) {
        return info instanceof TypeInfoArray && info.getBaseType().assignableFrom(type);
    }
}
