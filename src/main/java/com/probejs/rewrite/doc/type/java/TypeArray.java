package com.probejs.rewrite.doc.type.java;

import com.probejs.info.type.IType;
import com.probejs.rewrite.doc.type.DocType;
import com.probejs.rewrite.doc.type.DocTypeResolver;
import lombok.Getter;

@Getter
public class TypeArray implements DocType {

    private final DocType base;

    public TypeArray(IType type) {
        this.base = DocTypeResolver.of(type.getBase());
    }
}
