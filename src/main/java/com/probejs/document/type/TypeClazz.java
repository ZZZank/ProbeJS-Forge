package com.probejs.document.type;

import com.probejs.info.type.JavaTypeClass;

/**
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
        return "";
    }
}
