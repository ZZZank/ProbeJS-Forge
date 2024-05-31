package com.probejs.document.type;

import java.util.function.BiFunction;

/**
 * "Formatter & Document"
 * "string & number"
 * @author ZZZank
 */
public class TypeIntersection implements DocType {
    private final DocType leftType;
    private final DocType rightType;

    public TypeIntersection(DocType leftType, DocType rightType) {
        this.leftType = leftType;
        this.rightType = rightType;
    }

    @Override
    public String getTypeName() {
        return leftType.getTypeName() + " & " + rightType.getTypeName();
    }

    @Override
    public String transform(BiFunction<DocType, String, String> transformer) {
        return transformer.apply(this, leftType.transform(transformer) + " & " + rightType.transform(transformer));
    }
}
