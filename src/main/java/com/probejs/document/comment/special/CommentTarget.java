package com.probejs.document.comment.special;

import com.probejs.document.comment.SpecialComment;
import lombok.Getter;

@Getter
public class CommentTarget extends SpecialComment {

    private static final int MARK_LEN = "@target ".length();
    private final String targetName;

    public CommentTarget(String line) {
        super(line);
        targetName = line.substring(MARK_LEN);
    }
}
