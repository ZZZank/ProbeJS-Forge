package com.probejs.document.type;

import lombok.AllArgsConstructor;

import java.util.function.BiFunction;

/**
 * "Formatter & Document"
 * "string & number"
 * @author ZZZank
 */
@AllArgsConstructor
public class TypeIntersection implements DocType {
    private final DocType leftType;
    private final DocType rightType;

    @Override
    public String getTypeName() {
        return leftType.getTypeName() + " & " + rightType.getTypeName();
    }

    @Override
    public String transform(BiFunction<DocType, String, String> transformer) {
        return transformer.apply(this, leftType.transform(transformer) + " & " + rightType.transform(transformer));
    }
}
