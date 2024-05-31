package com.probejs.info.type;

import lombok.Data;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Data
public class JavaTypeParameterized implements JavaType {

    private JavaType rawType;
    private List<JavaType> paramTypes;

    public JavaTypeParameterized(Type type) {
        if (!test(type)) {
            throw new IllegalArgumentException("provided `type` is not an instance of ParameterizedType");
        }
        ParameterizedType parType = (ParameterizedType) type;
        rawType = TypeResolver.resolve(parType.getRawType());
        paramTypes = Arrays
                .stream(parType.getActualTypeArguments())
                .map(TypeResolver::resolve)
                .collect(Collectors.toList());
    }

    public JavaTypeParameterized(JavaType rawType, List<? extends JavaType> paramTypes) {
        this.rawType = rawType;
        this.paramTypes = new ArrayList<>(paramTypes);
    }

    public static boolean test(Type type) {
        return type instanceof ParameterizedType;
    }

    @Override
    public JavaType getBase() {
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
            paramTypes.stream().map(JavaType::getTypeName).collect(Collectors.joining(", "))
        );
    }

    @Override
    public JavaType copy() {
        return new JavaTypeParameterized(
            rawType.copy(),
            paramTypes.stream().map(JavaType::copy).collect(Collectors.toList())
        );
    }

    @Override
    public boolean assignableFrom(JavaType info) {
        if (info instanceof JavaTypeParameterized parType) {
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
