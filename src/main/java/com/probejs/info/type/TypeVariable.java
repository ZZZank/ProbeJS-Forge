package com.probejs.info.type;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TypeVariable implements IType {

    public static boolean test(Type type) {
        return type instanceof java.lang.reflect.TypeVariable;
    }

    private final java.lang.reflect.TypeVariable<?> raw;

    public TypeVariable(Type type) {
        this.raw = (java.lang.reflect.TypeVariable<?>) type;
    }

    private TypeVariable(java.lang.reflect.TypeVariable<?> inner) {
        this.raw = inner;
    }

    @Override
    public java.lang.reflect.TypeVariable<?> getRaw() {
        return this.raw;
    }

    @Override
    public IType getBaseType() {
        return this;
    }

    @Override
    public String getTypeName() {
        return this.raw.getTypeName();
    }

    @Override
    public IType copy() {
        return new TypeVariable(raw);
    }

    @Override
    public boolean assignableFrom(IType info) {
        return info instanceof TypeVariable;
    }

    @Override
    public Class<?> getResolvedClass() {
        return Object.class;
    }

    public List<IType> getBounds() {
        Type[] bounds = this.raw.getBounds();
        if (bounds.length == 1 && bounds[0] == Object.class) {
            return Collections.emptyList();
        }
        List<IType> boundTypes = new ArrayList<>(bounds.length);
        for (Type bound : bounds) {
            boundTypes.add(TypeResolver.resolveType(bound));
        }
        return boundTypes;
    }
}
