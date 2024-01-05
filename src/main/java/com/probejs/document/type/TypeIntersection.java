package com.probejs.document.type;

import java.util.function.BiFunction;

public class TypeIntersection implements IType {
    private final IType leftType;
    private final IType rightType;

    public TypeIntersection(IType leftType, IType rightType) {
        this.leftType = leftType;
        this.rightType = rightType;
    }

    @Override
    public String getTypeName() {
        return leftType.getTypeName() + " & " + rightType.getTypeName();
    }

    @Override
    public String getTransformedName(BiFunction<IType, String, String> transformer) {
        return transformer.apply(this, leftType.getTransformedName(transformer) + " & " + rightType.getTransformedName(transformer));
    }
}
