package com.probejs.info.type;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TypeInfoVariable implements ITypeInfo {

    public static boolean test(Type type) {
        return type instanceof TypeVariable;
    }

    private final TypeVariable<?> raw;
    private boolean underscored = false;

    public TypeInfoVariable(Type type) {
        this.raw = (TypeVariable<?>) type;
    }

    private TypeInfoVariable(TypeVariable<?> inner) {
        this.raw = inner;
    }

    public void setUnderscored(boolean underscored) {
        this.underscored = underscored;
    }

    @Override
    public TypeVariable<?> getRaw() {
        return this.raw;
    }

    @Override
    public ITypeInfo getBaseType() {
        return this;
    }

    @Override
    public String getTypeName() {
        return this.raw.getTypeName();
    }

    @Override
    public ITypeInfo copy() {
        TypeInfoVariable copied = new TypeInfoVariable(raw);
        copied.setUnderscored(underscored);
        return copied;
    }

    @Override
    public boolean assignableFrom(ITypeInfo info) {
        return info instanceof TypeInfoVariable;
    }

    @Override
    public Class<?> getResolvedClass() {
        return Object.class;
    }

    public List<ITypeInfo> getBounds() {
        Type[] bounds = this.raw.getBounds();
        if (bounds.length == 1 && bounds[0] == Object.class) {
            return Collections.emptyList();
        }
        List<ITypeInfo> boundTypes = new ArrayList<>(bounds.length);
        for (Type bound : bounds) {
            boundTypes.add(TypeResolver.resolveType(bound));
        }
        return boundTypes;
    }
}
