package com.probejs.document.comment.special;

import com.probejs.document.comment.AbstractComment;

public class CommentTarget extends AbstractComment {
    private static final int MARK_LEN = "@target ".length();
    private final String targetName;

    public CommentTarget(String line) {
        super(line);
        targetName = line.substring(MARK_LEN);
    }

    public String getTargetName() {
        return targetName;
    }
}
