package com.probejs.rewrite.doc.type;

import com.probejs.info.clazz.ClassInfo;
import com.probejs.rewrite.ClazzPath;
import com.probejs.rewrite.doc.DocClazz;
import lombok.Getter;

@Getter
public class DocTypeClazz implements DocType {

    private final ClazzPath path;
    private final DocClazz doc;
    private final boolean assigned;

    DocTypeClazz(Class<?> clazz) {
        this.doc = DocClazz.of(clazz);
        this.path = this.doc.getPath();
        this.assigned = !this.doc.getAssignables().isEmpty();
    }

    DocTypeClazz(ClassInfo clazz) {
        this(clazz.getRaw());
    }
}
