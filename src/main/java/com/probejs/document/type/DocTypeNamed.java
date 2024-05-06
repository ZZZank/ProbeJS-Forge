package com.probejs.document.type;

import com.probejs.formatter.resolver.NameResolver;

public class DocTypeNamed implements IDocType {

    private final String typeName;

    public DocTypeNamed(String typeName) {
        this.typeName = typeName;
    }

    public String getRawTypeName() {
        return typeName;
    }

    @Override
    public String getTypeName() {
        NameResolver.ResolvedName resolved = NameResolver.resolvedNames.get(typeName);
        if (resolved == null) {
            return typeName;
        }
        return resolved.getFullName();
    }
}
