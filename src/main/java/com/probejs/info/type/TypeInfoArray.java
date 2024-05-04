package com.probejs.info.type;

import lombok.Setter;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.util.List;

@Setter
public class TypeInfoArray implements ITypeInfo {

    /**
     * inner type, e.g. "T" in "T[]"
     */
    private ITypeInfo base;

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

    public TypeInfoArray(Type type) {
        if (isGenericArray(type)) {
            this.base = TypeResolver.resolveType(((GenericArrayType) type).getGenericComponentType());
        } else if (isClassArray(type)) {
            assert type instanceof Class<?>;
            this.base = TypeResolver.resolveType(((Class<?>) type).getComponentType());
        } else {
            throw new IllegalArgumentException("Argument 'type' is not array");
        }
    }

    private TypeInfoArray(ITypeInfo inner) {
        this.base = inner;
    }

    @Override
    public ITypeInfo getBaseType() {
        return base;
    }

    @Override
    public Class<?> getResolvedClass() {
        return List.class;
    }

    @Override
    public String getTypeName() {
        return this.base.getTypeName()+"[]";
    }

    @Override
    public ITypeInfo copy() {
        return new TypeInfoArray(base.copy());
    }

    @Override
    public boolean assignableFrom(ITypeInfo info) {
        return info instanceof TypeInfoArray && info.getBaseType().assignableFrom(base);
    }

    @Override
    public Type getRaw() {
        return this.base.getRaw();
    }
}
