package com.probejs.document.type;

import com.probejs.formatter.resolver.PathResolver;
import java.util.function.BiFunction;

public interface DocType {
    /**
     * performs NO processing
     * @return the provided string itself
     */
    BiFunction<DocType, String, String> dummyTransformer = (type, raw) -> raw;
    /**
     * Add an underscore to string {@code raw} if:<p>
     * 1. type is an instance of DocTypeNamed, and<p>
     * 2. name of this type is resolved ,and<p>
     * 1. this type is not primitive type that should be skipped<p>
     * otherwise, return {@code raw} itself
     */
    BiFunction<DocType, String, String> defaultTransformer = (type, raw) -> {
        if (!(type instanceof TypeNamed)) {
            return raw;
        }
        String rawTypeName = ((TypeNamed) type).getRawTypeName();
        if (
            PathResolver.resolved.containsKey(rawTypeName) &&
            !PathResolver.resolvedPrimitives.contains(rawTypeName)
        ) {
            return raw + "_";
        }
        return raw;
    };

    String getTypeName();

    /**
     * NOTE: Complex types will and should have every sub-type transformed
     * @param transformer A fn that accepts one IDocType and raw String, and returns transformed String
     * @return The transformed name after being processed by {@code transformer}
     * @see DocType#defaultTransformer
     * @see DocType#dummyTransformer
     */
    default String transform(BiFunction<DocType, String, String> transformer) {
        return transformer.apply(this, getTypeName());
    }
}
