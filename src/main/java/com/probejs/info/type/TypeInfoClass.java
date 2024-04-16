package com.probejs.info.type;

import com.probejs.info.ClassInfo;

import java.lang.reflect.Type;
import java.util.List;

public class TypeInfoClass implements ITypeInfo {

    public static boolean test(Type type) {
        return type instanceof Class<?>;
    }

    private final ClassInfo info;

    public TypeInfoClass(Type type) {
        if (!(type instanceof Class<?>)){
            throw new IllegalArgumentException();
        }
        this.info = ClassInfo.ofCache((Class<?>) type);
    }

    private TypeInfoClass(ClassInfo type) {
        info = type;
    }

    @Override
    public ITypeInfo getBaseType() {
        return this;
    }

    @Override
    public Class<?> getResolvedClass() {
        return this.info.getRaw();
    }

    @Override
    public String getTypeName() {
        return this.info.getRaw().getTypeName();
    }

    @Override
    public ITypeInfo copy() {
        return new TypeInfoClass(this.info);
    }

    @Override
    public boolean assignableFrom(ITypeInfo info) {
        if (!(info instanceof TypeInfoClass)) {
            return false;
        }
        TypeInfoClass clazz = (TypeInfoClass) info;
        return clazz.info.getRaw().isAssignableFrom(this.info.getRaw());
    }

    public List<TypeInfoVariable> getTypeVariables() {
        return this.info.getTypeParameters();
    }

    @Override
    public Type getRaw() {
        return this.info.getRaw();
    }
}
