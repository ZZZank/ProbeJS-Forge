package com.probejs.info.type;

import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TypeInfoWildcard implements ITypeInfo {

    public static boolean test(Type type) {
        return type instanceof WildcardType;
    }

    private final WildcardType raw;
    private final ITypeInfo base;
    private final List<ITypeInfo> upper;
    private final List<ITypeInfo> lower;

    public TypeInfoWildcard(Type type) {
        this.raw = (WildcardType) type;

        Type[] upper = this.raw.getUpperBounds();
        Type[] lower = this.raw.getLowerBounds();

        if (upper[0] != Object.class) {
            this.base = TypeResolver.resolveType(upper[0]);
        } else if (lower.length != 0) {
            this.base = TypeResolver.resolveType(lower[0]);
        } else {
            this.base = new TypeInfoClass(Object.class);
        }

        this.upper = upper[0] == Object.class
            ? Collections.emptyList()
            : Arrays.stream(upper).map(TypeResolver::resolveType).collect(Collectors.toList());

        this.lower = lower.length == 0
            ? Collections.emptyList()
            : Arrays.stream(lower).map(TypeResolver::resolveType).collect(Collectors.toList());
    }

    @Override
    public ITypeInfo getBaseType() {
        return base;
    }

    @Override
    public Class<?> getResolvedClass() {
        return base.getResolvedClass();
    }

    @Override
    public String getTypeName() {
        return this.base.getTypeName();
    }

    @Override
    public ITypeInfo copy() {
        return new TypeInfoWildcard(this.raw);
    }

    @Override
    public boolean assignableFrom(ITypeInfo info) {
        return info.getBaseType().assignableFrom(base);
    }

    @Override
    public Type getRaw() {
        return this.raw;
    }

    public List<ITypeInfo> getUpperBounds() {
        return this.upper;
    }

    public List<ITypeInfo> getLowerBounds() {
        return this.lower;
    }
}
