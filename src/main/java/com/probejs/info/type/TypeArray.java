package com.probejs.info.type;

import lombok.Setter;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.util.List;

@Setter
public class TypeArray implements IType {

    /**
     * inner type, e.g. "T" in "T[]"
     */
    private IType base;

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

    public TypeArray(Type type) {
        if (isGenericArray(type)) {
            this.base = TypeResolver.resolveType(((GenericArrayType) type).getGenericComponentType());
        } else if (isClassArray(type)) {
            assert type instanceof Class<?>;
            this.base = TypeResolver.resolveType(((Class<?>) type).getComponentType());
        } else {
            throw new IllegalArgumentException("Argument 'type' is not array");
        }
    }

    private TypeArray(IType inner) {
        this.base = inner;
    }

    @Override
    public IType getBaseType() {
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
    public IType copy() {
        return new TypeArray(base.copy());
    }

    @Override
    public boolean assignableFrom(IType info) {
        return info instanceof TypeArray && info.getBaseType().assignableFrom(base);
    }

    @Override
    public Type getRaw() {
        return this.base.getRaw();
    }
}
