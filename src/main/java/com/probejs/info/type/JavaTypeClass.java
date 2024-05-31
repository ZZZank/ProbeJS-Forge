package com.probejs.info.type;

import com.probejs.info.clazz.ClassInfo;
import lombok.Data;
import lombok.Getter;

import java.lang.reflect.Type;
import java.util.List;

@Getter
@Data
public class JavaTypeClass implements JavaType {

    public static boolean test(Type type) {
        return type instanceof Class<?>;
    }

    private final Class<?> raw;

    public JavaTypeClass(Type type) {
        this.raw = (Class<?>) type;
    }

    @Override
    public JavaType getBase() {
        return this;
    }

    @Override
    public Class<?> getResolvedClass() {
        return raw;
    }

    @Override
    public String getTypeName() {
        return this.raw.getTypeName();
    }

    @Override
    public JavaType copy() {
        return new JavaTypeClass(raw);
    }

    @Override
    public boolean assignableFrom(JavaType info) {
        if (!(info instanceof JavaTypeClass clazz)) {
            return false;
        }
        return clazz.raw.isAssignableFrom(raw);
    }

    public List<JavaTypeVariable> getTypeVariables() {
        return ClassInfo.ofCache(this.raw).getTypeParameters();
    }
}
