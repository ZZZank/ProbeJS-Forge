package com.probejs.document.type;

import com.probejs.formatter.resolver.PathResolver;
import com.probejs.formatter.resolver.ClassPath;
import lombok.AllArgsConstructor;

/**
 * also literal, but allows underscore, and will check resolved names from {@link PathResolver#resolved}
 */
@AllArgsConstructor
public class TypeNamed implements DocType {

    private final String name;

    public String getRawTypeName() {
        return name;
    }

    @Override
    public String getTypeName() {
        ClassPath resolved = PathResolver.resolved.get(name);
        if (resolved == null) {
            return name;
        }
        return resolved.fullPath();
    }
}
