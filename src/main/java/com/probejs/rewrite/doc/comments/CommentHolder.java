package com.probejs.rewrite.doc.comments;

import com.probejs.document.DocComment;

public interface CommentHolder {

    DocComment getComment();

    void applyComment();
}
