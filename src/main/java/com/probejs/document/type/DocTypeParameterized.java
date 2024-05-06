package com.probejs.document.type;

import lombok.Getter;

import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Getter
public class DocTypeParameterized implements IDocType {

    private final IDocType rawType;
    private final List<IDocType> paramTypes;

    public DocTypeParameterized(IDocType rawType, List<IDocType> paramTypes) {
        this.rawType = rawType;
        this.paramTypes = paramTypes;
    }

    @Override
    public String getTypeName() {
        return String.format(
            "%s<%s>",
            rawType.getTypeName(),
            paramTypes.stream().map(IDocType::getTypeName).collect(Collectors.joining(", "))
        );
    }

    @Override
    public String transform(BiFunction<IDocType, String, String> transformer) {
        return transformer.apply(this, String.format("%s<%s>",rawType.transform(transformer), paramTypes.stream().map(t -> t.transform(transformer)).collect(Collectors.joining(", "))));
    }
}
