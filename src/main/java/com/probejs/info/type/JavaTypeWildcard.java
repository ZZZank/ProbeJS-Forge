package com.probejs.info.type;

import lombok.Data;

import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class JavaTypeWildcard implements JavaType {

    public static boolean test(Type type) {
        return type instanceof WildcardType;
    }

    private final WildcardType raw;
    private final JavaType base;
    private final List<JavaType> upperBounds;
    private final List<JavaType> lowerBounds;

    public JavaTypeWildcard(Type type) {
        this.raw = (WildcardType) type;

        Type[] upper = this.raw.getUpperBounds();
        Type[] lower = this.raw.getLowerBounds();

        if (upper[0] != Object.class) {
            this.base = TypeResolver.resolve(upper[0]);
        } else if (lower.length != 0) {
            this.base = TypeResolver.resolve(lower[0]);
        } else {
            this.base = new JavaTypeClass(Object.class);
        }

        this.upperBounds = upper[0] == Object.class
            ? Collections.emptyList()
            : Arrays.stream(upper).map(TypeResolver::resolve).collect(Collectors.toList());

        this.lowerBounds = lower.length == 0
            ? Collections.emptyList()
            : Arrays.stream(lower).map(TypeResolver::resolve).collect(Collectors.toList());
    }

    @Override
    public JavaType getBase() {
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
    public JavaType copy() {
        return new JavaTypeWildcard(this.raw);
    }

    @Override
    public boolean assignableFrom(JavaType info) {
        return info.getBase().assignableFrom(base);
    }

}
