package com.probejs.rewrite.doc.type;

import com.probejs.info.type.IType;
import com.probejs.info.type.TypeArray;
import com.probejs.info.type.TypeClass;
import com.probejs.rewrite.doc.type.java.TypeClazz;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public abstract class DocTypeResolver {

    private static final Map<Class<? extends IType>, Function<IType, DocType>> REGISTRIES;

    static {
        REGISTRIES = new HashMap<>();
        REGISTRIES.put(TypeClass.class, TypeClazz::new);
        REGISTRIES.put(TypeArray.class, com.probejs.rewrite.doc.type.java.TypeArray::new);
    }

    public static DocType of(IType type) {
        return REGISTRIES.get(type.getClass()).apply(type);
    }
}
