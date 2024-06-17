package com.probejs.document.type;

import com.probejs.formatter.resolver.PathResolver;
import com.probejs.info.type.JavaTypeClass;

/**
 * "String", "Map"
 * note that there's no attached type variables
 * @author ZZZank
 */
public class TypeClazz implements DocType {

    private final Class<?> raw;

    public TypeClazz(Class<?> raw) {
        this.raw = raw;
    }

    public TypeClazz(JavaTypeClass jType) {
        this.raw = jType.getRaw();
    }

    @Override
    public String getTypeName() {
        return PathResolver.getResolvedName(raw.getName()).fullPath();
    }
}
