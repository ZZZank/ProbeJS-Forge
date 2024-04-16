package com.probejs.info.type;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TypeInfoParameterized implements ITypeInfo {

    private ITypeInfo rawType;
    private List<ITypeInfo> paramTypes;
    public TypeInfoParameterized(Type type) {
        if (!test(type)) {
            throw new IllegalArgumentException("provided `type` is not an instance of ParameterizedType");
        }
        ParameterizedType parType = (ParameterizedType) type;
        rawType = TypeResolver.resolveType(parType.getRawType());
        paramTypes =
            Arrays
                .stream(parType.getActualTypeArguments())
                .map(TypeResolver::resolveType)
                .collect(Collectors.toList());
    }

    public TypeInfoParameterized(ITypeInfo rawType, List<? extends ITypeInfo> paramTypes) {
        this.rawType = rawType;
        this.paramTypes = new ArrayList<>(paramTypes);
    }

    public static boolean test(Type type) {
        return type instanceof ParameterizedType;
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

    public void setParamTypes(List<ITypeInfo> paramTypes) {
        this.paramTypes = paramTypes;
    }

    @Override
    public String getTypeName() {
        return String.format("%s<%s>",
            this.rawType.getTypeName(),
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

    public void setRawType(ITypeInfo rawType) {
        this.rawType = rawType;
    }

    @Override
    public Type getRaw() {
        return this.rawType.getRaw();
    }
}
