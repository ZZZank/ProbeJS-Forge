package com.probejs.document.type;

import com.probejs.formatter.resolver.NameResolver;
import com.probejs.info.type.JavaTypeClass;
import lombok.val;

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
        return NameResolver.getResolvedName(raw.getName()).getFullName();
    }
}
