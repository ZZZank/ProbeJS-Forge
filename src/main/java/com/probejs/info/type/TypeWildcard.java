package com.probejs.info.type;

import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TypeWildcard implements IType {

    public static boolean test(Type type) {
        return type instanceof WildcardType;
    }

    private final WildcardType raw;
    private final IType base;
    private final List<IType> upper;
    private final List<IType> lower;

    public TypeWildcard(Type type) {
        this.raw = (WildcardType) type;

        Type[] upper = this.raw.getUpperBounds();
        Type[] lower = this.raw.getLowerBounds();

        if (upper[0] != Object.class) {
            this.base = TypeResolver.resolveType(upper[0]);
        } else if (lower.length != 0) {
            this.base = TypeResolver.resolveType(lower[0]);
        } else {
            this.base = new TypeClass(Object.class);
        }

        this.upper = upper[0] == Object.class
            ? Collections.emptyList()
            : Arrays.stream(upper).map(TypeResolver::resolveType).collect(Collectors.toList());

        this.lower = lower.length == 0
            ? Collections.emptyList()
            : Arrays.stream(lower).map(TypeResolver::resolveType).collect(Collectors.toList());
    }

    @Override
    public IType getBaseType() {
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
    public IType copy() {
        return new TypeWildcard(this.raw);
    }

    @Override
    public boolean assignableFrom(IType info) {
        return info.getBaseType().assignableFrom(base);
    }

    @Override
    public Type getRaw() {
        return this.raw;
    }

    public List<IType> getUpperBounds() {
        return this.upper;
    }

    public List<IType> getLowerBounds() {
        return this.lower;
    }
}
