package com.probejs.info.type;

import lombok.Data;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Data
public class TypeParameterized implements IType {

    private IType rawType;
    private List<IType> paramTypes;

    public TypeParameterized(Type type) {
        if (!test(type)) {
            throw new IllegalArgumentException("provided `type` is not an instance of ParameterizedType");
        }
        ParameterizedType parType = (ParameterizedType) type;
        rawType = TypeResolver.resolveType(parType.getRawType());
        paramTypes = Arrays
                .stream(parType.getActualTypeArguments())
                .map(TypeResolver::resolveType)
                .collect(Collectors.toList());
    }

    public TypeParameterized(IType rawType, List<? extends IType> paramTypes) {
        this.rawType = rawType;
        this.paramTypes = new ArrayList<>(paramTypes);
    }

    public static boolean test(Type type) {
        return type instanceof ParameterizedType;
    }

    @Override
    public IType getBase() {
        return rawType;
    }

    @Override
    public Class<?> getResolvedClass() {
        return rawType.getResolvedClass();
    }

    @Override
    public String getTypeName() {
        return String.format("%s<%s>",
            this.rawType.getTypeName(),
            paramTypes.stream().map(IType::getTypeName).collect(Collectors.joining(", "))
        );
    }

    @Override
    public IType copy() {
        return new TypeParameterized(
            rawType.copy(),
            paramTypes.stream().map(IType::copy).collect(Collectors.toList())
        );
    }

    @Override
    public boolean assignableFrom(IType info) {
        if (info instanceof TypeParameterized) {
            TypeParameterized parType = (TypeParameterized) info;
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

    @Override
    public Type getRaw() {
        return this.rawType.getRaw();
    }
}
