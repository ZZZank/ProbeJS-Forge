package com.probejs.document.type;

import java.util.function.BiFunction;

public class DocTypeIntersection implements IDocType {
    private final IDocType leftType;
    private final IDocType rightType;

    public DocTypeIntersection(IDocType leftType, IDocType rightType) {
        this.leftType = leftType;
        this.rightType = rightType;
    }

    @Override
    public String getTypeName() {
        return leftType.getTypeName() + " & " + rightType.getTypeName();
    }

    @Override
    public String transform(BiFunction<IDocType, String, String> transformer) {
        return transformer.apply(this, leftType.transform(transformer) + " & " + rightType.transform(transformer));
    }
}
