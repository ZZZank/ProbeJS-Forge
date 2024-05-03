package com.probejs.info.type.ts;

import com.probejs.info.type.ITypeInfo;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.Map;

public class TypeObject implements ITypeInfo {
    private final Map<String, ITypeInfo> raw;

    TypeObject(Map<String, ITypeInfo> types) {
        this.raw = types;
    }

    @Nullable
    @Override
    public Type getRaw() {
        return null;
    }

    @Override
    public ITypeInfo getBaseType() {
        return null;
    }

    @Nullable
    @Override
    public Class<?> getResolvedClass() {
        return null;
    }

    @Override
    public String getTypeName() {
        return "";
    }

    @Override
    public ITypeInfo copy() {
        return null;
    }

    @Override
    public boolean assignableFrom(ITypeInfo info) {
        return false;
    }
}
