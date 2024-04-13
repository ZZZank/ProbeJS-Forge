package com.probejs.document.type;

import com.probejs.formatter.NameResolver;
import java.util.function.BiFunction;

public interface IType {
    /**
     * performs NO processing
     * @return the provided string itself
     */
    BiFunction<IType, String, String> dummyTransformer = (type, raw) -> raw;
    /**
     * Add an underscore to string {@code raw} if:<p>
     * 1. type is an instance of TypeNamed, and<p>
     * 2. name of this type is resolved ,and<p>
     * 1. this type is not primitive type that should be skipped<p>
     * otherwise, return {@code raw} itself
     */
    BiFunction<IType, String, String> defaultTransformer = (type, raw) -> {
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
     * @see com.probejs.document.type.IType#defaultTransformer
     * @see com.probejs.document.type.IType#dummyTransformer
     */
    default String transform(BiFunction<IType, String, String> transformer) {
        return transformer.apply(this, getTypeName());
    }
}
