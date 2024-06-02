package com.probejs.document.type;

import com.probejs.formatter.resolver.NameResolver;
import lombok.AllArgsConstructor;

/**
 * also literal, but allows underscore, and will check resolved names from {@link NameResolver#resolvedNames}
 */
@AllArgsConstructor
public class TypeNamed implements DocType {

    private final String name;

    public String getRawTypeName() {
        return name;
    }

    @Override
    public String getTypeName() {
        NameResolver.ResolvedName resolved = NameResolver.resolvedNames.get(name);
        if (resolved == null) {
            return name;
        }
        return resolved.getFullName();
    }
}
