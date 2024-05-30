package com.probejs.document.comment.special;

import com.probejs.document.comment.SpecialComment;

public class CommentHidden extends SpecialComment {

    public static final String MARK = "@hidden";

    public CommentHidden(String line) {
        super(line);
    }
}
