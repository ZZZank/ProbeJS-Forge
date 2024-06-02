package com.probejs.document.type;

import com.probejs.info.type.JavaTypeArray;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.function.BiFunction;

/**
 * "string[]"
 * @author ZZZank
 */
@AllArgsConstructor
@Getter
public class TypeArray implements DocType {
    private final DocType component;

    public TypeArray(JavaTypeArray jArr) {
        this.component = DocTypeResolver.fromJava(jArr.getBase());
    }

    @Override
    public String getTypeName() {
        return component.getTypeName() + "[]";
    }

    @Override
    public String transform(BiFunction<DocType, String, String> transformer) {
        return transformer.apply(this, component.transform(transformer) + "[]");
    }
}
