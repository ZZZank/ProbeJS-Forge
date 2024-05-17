package com.probejs.rewrite.doc.comments;

import com.probejs.rewrite.doc.DocComment;

public interface CommentHolder {

    DocComment getComment();

    void applyComment();
}
