package com.probejs.rewrite.doc.type.java;

import com.probejs.info.clazz.ClassInfo;
import com.probejs.info.type.IType;
import com.probejs.info.type.TypeClass;
import com.probejs.rewrite.ClazzPath;
import com.probejs.rewrite.doc.DocClazz;
import com.probejs.rewrite.doc.type.DocType;
import lombok.Getter;

@Getter
public class TypeClazz implements DocType {

    private final ClazzPath path;
    private final DocClazz doc;
    private final boolean assigned;

    TypeClazz(Class<?> clazz) {
        this.doc = DocClazz.of(clazz);
        this.path = this.doc.getPath();
        this.assigned = !this.doc.getAssignables().isEmpty();
    }

    TypeClazz(ClassInfo clazz) {
        this(clazz.getRaw());
    }

    public TypeClazz(IType iType) {
        this(((TypeClass) iType).getRaw());
    }
}
