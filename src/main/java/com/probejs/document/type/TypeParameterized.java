package com.probejs.document.type;

import com.probejs.info.type.JavaTypeParameterized;
import lombok.Getter;

import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Getter
public class TypeParameterized implements DocType {

    private final DocType rawType;
    private final List<DocType> paramTypes;

    public TypeParameterized(DocType rawType, List<DocType> paramTypes) {
        this.rawType = rawType;
        this.paramTypes = paramTypes;
    }

    public TypeParameterized(JavaTypeParameterized jType) {
        this.rawType = DocTypeResolver.fromJava(jType.getRawType());
        this.paramTypes = jType.getParamTypes().stream().map(DocTypeResolver::fromJava).collect(Collectors.toList());
    }

    @Override
    public String getTypeName() {
        return String.format(
            "%s<%s>",
            rawType.getTypeName(),
            paramTypes.stream().map(DocType::getTypeName).collect(Collectors.joining(", "))
        );
    }

    @Override
    public String transform(BiFunction<DocType, String, String> transformer) {
        return transformer.apply(this, String.format("%s<%s>",rawType.transform(transformer), paramTypes.stream().map(t -> t.transform(transformer)).collect(Collectors.joining(", "))));
    }
}
