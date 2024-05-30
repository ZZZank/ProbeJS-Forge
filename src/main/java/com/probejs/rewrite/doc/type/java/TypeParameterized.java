package com.probejs.rewrite.doc.type.java;

import com.probejs.rewrite.doc.type.DocTypeResolver;
import lombok.val;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * "List<String>"
 * @author ZZZank
 */
public class TypeParameterized implements JavaType {

    private final JavaType base;
    private final List<JavaType> params;

    public TypeParameterized(Type parameterized) {
        assert parameterized instanceof ParameterizedType;
        val type = (ParameterizedType) parameterized;
        this.base = DocTypeResolver.ofJava(type.getRawType());
        this.params = Arrays.stream(type.getActualTypeArguments())
            .map(DocTypeResolver::ofJava)
            .collect(Collectors.toList());
    }

    @Override
    public Type raw() {
        return null;
    }

    @Override
    public JavaType base() {
        return base;
    }

    @Override
    public Collection<Class<?>> relatedClasses() {
        val related = new ArrayList<Class<?>>(1+params.size()+1);
        related.addAll(base.relatedClasses());
        for (val param : params) {
            related.addAll(param.relatedClasses());
        }
        return related;
    }
}
