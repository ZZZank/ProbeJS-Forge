package com.probejs.info.type;

import lombok.Data;

import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class TypeWildcard implements IType {

    public static boolean test(Type type) {
        return type instanceof WildcardType;
    }

    private final WildcardType raw;
    private final IType base;
    private final List<IType> upperBounds;
    private final List<IType> lowerBounds;

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

        this.upperBounds = upper[0] == Object.class
            ? Collections.emptyList()
            : Arrays.stream(upper).map(TypeResolver::resolveType).collect(Collectors.toList());

        this.lowerBounds = lower.length == 0
            ? Collections.emptyList()
            : Arrays.stream(lower).map(TypeResolver::resolveType).collect(Collectors.toList());
    }

    @Override
    public IType getBase() {
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
        return info.getBase().assignableFrom(base);
    }

}
