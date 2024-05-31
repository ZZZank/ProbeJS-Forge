package com.probejs.info.type;

import com.probejs.info.clazz.ClassInfo;
import lombok.Data;
import lombok.Getter;

import java.lang.reflect.Type;
import java.util.List;

@Getter
@Data
public class TypeClass implements IType {

    public static boolean test(Type type) {
        return type instanceof Class<?>;
    }

    private final Class<?> raw;

    public TypeClass(Type type) {
        this.raw = (Class<?>) type;
    }

    @Override
    public IType getBase() {
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
    public IType copy() {
        return new TypeClass(raw);
    }

    @Override
    public boolean assignableFrom(IType info) {
        if (!(info instanceof TypeClass clazz)) {
            return false;
        }
        return clazz.raw.isAssignableFrom(raw);
    }

    public List<TypeVariable> getTypeVariables() {
        return ClassInfo.ofCache(this.raw).getTypeParameters();
    }
}
