package com.probejs.rewrite.doc;

import com.probejs.info.clazz.ClassInfo;
import com.probejs.rewrite.doc.comments.CommentHolder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class DocClazz implements CommentHolder {

    private final List<DocMethod> methods;
    private final List<DocField> fields;
    private final DocComment comment;

    DocClazz(ClassInfo cInfo) {
        this.comment = new DocComment();
        this.fields = cInfo.getFields().stream().map(DocField::new).collect(Collectors.toList());
        this.methods = cInfo.getMethods().stream().map(DocMethod::new).collect(Collectors.toList());
    }

    @Override
    public void applyComment() {

    }
}
