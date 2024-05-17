package com.probejs.rewrite.doc;

import com.probejs.info.clazz.FieldInfo;
import com.probejs.info.type.IType;
import com.probejs.rewrite.doc.comments.CommentHolder;
import lombok.Getter;

@Getter
public class DocField implements CommentHolder {

    private final DocComment comment;
    private String name;
    private IType type;

    DocField(FieldInfo raw) {
        this.comment = new DocComment();
        //TODO: doc type
        this.name = raw.getName();
        this.type = raw.getType();
    }

    @Override
    public void applyComment() {

    }
}
