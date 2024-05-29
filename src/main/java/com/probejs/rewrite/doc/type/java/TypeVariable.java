package com.probejs.rewrite.doc.type.java;

import com.probejs.info.type.TypeResolver;
import com.probejs.rewrite.doc.type.DocTypeResolver;
import lombok.AllArgsConstructor;
import lombok.val;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author ZZZank
 */
@AllArgsConstructor
public class TypeVariable implements JavaType {
    private final java.lang.reflect.TypeVariable<?> raw;

    @Override
    public Type raw() {
        return raw;
    }

    @Override
    public JavaType base() {
        return this;
    }

    @Override
    public Collection<Class<?>> relatedClasses() {
        val related = new ArrayList<Class<?>>();
        for (Type bound : raw.getBounds()) {

        }
        return related;
    }
}
