package com.probejs.info.type;

import lombok.Data;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
public class JavaTypeVariable implements JavaType {

    public static boolean test(Type type) {
        return type instanceof java.lang.reflect.TypeVariable;
    }

    private final java.lang.reflect.TypeVariable<?> raw;

    public JavaTypeVariable(Type type) {
        this.raw = (java.lang.reflect.TypeVariable<?>) type;
    }

    @Override
    public java.lang.reflect.TypeVariable<?> getRaw() {
        return this.raw;
    }

    @Override
    public JavaType getBase() {
        return this;
    }

    @Override
    public String getTypeName() {
        return this.raw.getTypeName();
    }

    @Override
    public JavaType copy() {
        return new JavaTypeVariable(raw);
    }

    @Override
    public boolean assignableFrom(JavaType info) {
        return info instanceof JavaTypeVariable;
    }

    @Override
    public Class<?> getResolvedClass() {
        return Object.class;
    }

    public List<JavaType> getBounds() {
        Type[] bounds = this.raw.getBounds();
        if (bounds.length == 1 && bounds[0] == Object.class) {
            return Collections.emptyList();
        }
        List<JavaType> boundTypes = new ArrayList<>(bounds.length);
        for (Type bound : bounds) {
            boundTypes.add(TypeResolver.resolve(bound));
        }
        return boundTypes;
    }
}
