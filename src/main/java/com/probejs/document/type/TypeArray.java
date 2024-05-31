package com.probejs.document.type;

import com.probejs.info.type.JavaTypeArray;
import lombok.Getter;

import java.util.function.BiFunction;

/**
 * "string[]"
 * @author ZZZank
 */
@Getter
public class TypeArray implements DocType {
    private final DocType base;

    public TypeArray(DocType component) {
        this.base = component;
    }

    public TypeArray(JavaTypeArray jArr) {
        this.base = DocTypeResolver.fromJava(jArr.getBase());
    }

    @Override
    public String getTypeName() {
        return base.getTypeName() + "[]";
    }

    @Override
    public String transform(BiFunction<DocType, String, String> transformer) {
        return transformer.apply(this, base.transform(transformer) + "[]");
    }
}
