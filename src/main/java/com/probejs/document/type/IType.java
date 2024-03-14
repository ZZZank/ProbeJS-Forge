package com.probejs.document.type;

import com.probejs.formatter.NameResolver;
import java.util.function.BiFunction;

public interface IType {
    /**
     * performs NO processing
     */
    BiFunction<IType, String, String> dummyTransformer = (type, raw) -> raw;
    BiFunction<IType, String, String> underscoreTransformer = (type, raw) -> {
        if (!(type instanceof TypeNamed)) {
            return raw;
        }
        String rawTypeName = ((TypeNamed) type).getRawTypeName();
        if (
            NameResolver.resolvedNames.containsKey(rawTypeName) &&
            !NameResolver.resolvedPrimitives.contains(rawTypeName)
        ) {
            return raw + "_";
        }
        return raw;
    };

    String getTypeName();

    /**
     * NOTE: Complex types will and should have every sub-type transformed
     * @param transformer A fn that accepts one IType and raw String, and returns transformed String
     * @return The transformed name after being processed by {@code transformer}
     * @see com.probejs.document.type.IType#underscoreTransformer
     * @see com.probejs.document.type.IType#dummyTransformer
     */
    default String transform(BiFunction<IType, String, String> transformer) {
        return transformer.apply(this, getTypeName());
    }
}
