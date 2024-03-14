package com.probejs.info.type;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TypeInfoParameterized implements ITypeInfo {

    public static boolean test(Type type) {
        return type instanceof ParameterizedType;
    }

    private ITypeInfo rawType;
    private List<ITypeInfo> paramTypes;

    public TypeInfoParameterized(Type type) {
        if (!(type instanceof ParameterizedType)) {
            throw new IllegalArgumentException("provided `type` is not an instance of ParameterizedType");
        }
        ParameterizedType parType = (ParameterizedType) type;
        rawType = InfoTypeResolver.resolveType(parType.getRawType());
        paramTypes =
            Arrays
                .stream(parType.getActualTypeArguments())
                .map(InfoTypeResolver::resolveType)
                .collect(Collectors.toList());
    }

    public TypeInfoParameterized(ITypeInfo rawType, List<ITypeInfo> paramTypes) {
        this.rawType = rawType;
        this.paramTypes = paramTypes;
    }

    @Override
    public ITypeInfo getBaseType() {
        return rawType;
    }

    @Override
    public Class<?> getResolvedClass() {
        return rawType.getResolvedClass();
    }

    public List<ITypeInfo> getParamTypes() {
        return paramTypes;
    }

    @Override
    public String getTypeName() {
        return wrapTypeName(this.rawType.getTypeName());
    }

    @Override
    public String wrapTypeName(String rawName) {
        return String.format(
            "%s<%s>",
            rawName,
            paramTypes.stream().map(ITypeInfo::getTypeName).collect(Collectors.joining(", "))
        );
    }

    @Override
    public ITypeInfo copy() {
        return new TypeInfoParameterized(
            rawType.copy(),
            paramTypes.stream().map(ITypeInfo::copy).collect(Collectors.toList())
        );
    }

    @Override
    public boolean assignableFrom(ITypeInfo info) {
        if (info instanceof TypeInfoParameterized) {
            TypeInfoParameterized parType = (TypeInfoParameterized) info;
            if (parType.rawType.assignableFrom(rawType) && parType.paramTypes.size() == paramTypes.size()) {
                for (int i = 0; i < paramTypes.size(); i++) {
                    if (!parType.paramTypes.get(i).assignableFrom(paramTypes.get(i))) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    public void setParamTypes(List<ITypeInfo> paramTypes) {
        this.paramTypes = paramTypes;
    }

    public void setRawType(ITypeInfo rawType) {
        this.rawType = rawType;
    }
}
