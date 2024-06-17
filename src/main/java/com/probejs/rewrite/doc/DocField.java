package com.probejs.rewrite.doc;

import com.probejs.document.type.DocType;
import com.probejs.document.type.DocTypeResolver;
import com.probejs.info.clazz.FieldInfo;
import com.probejs.rewrite.doc.comments.CommentHolder;
import lombok.Getter;
import lombok.Setter;
import lombok.val;

@Getter
@Setter
public class DocField implements CommentHolder {

    private final DocComment comment;
    private String name;
    private DocType type;

    DocField(FieldInfo raw) {
        this.comment = new DocComment();
        this.name = raw.getName();
        this.type = DocTypeResolver.fromJava(raw.getType());
    }

    @Override
    public void applyComment() {
        val specials = this.comment.getSpecials();
    }
}
