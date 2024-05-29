package com.probejs.rewrite.doc.type;

import com.probejs.info.type.IType;
import com.probejs.info.type.TypeArray;
import lombok.Getter;

@Getter
public class DocTypeArray implements DocType {

    private final DocType base;

    DocTypeArray(IType type) {
        this.base = DocTypeResolver.of(((TypeArray)type).getBase());
    }
}
