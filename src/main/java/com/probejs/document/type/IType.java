package com.probejs.document.type;

import java.util.function.BiFunction;

public interface IType {
    String getTypeName();

    /**
     * NOTE: Complex types will and should have every sub-type transformed
     * @param transformer A fn that accepts one IType and raw String, and returns transformed String
     * @return The transformed name after being processed by {@code transformer}
     */
    default String getTransformedName(BiFunction<IType, String, String> transformer) {
        return transformer.apply(this, getTypeName());
    }
}
