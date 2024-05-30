package com.probejs.document.comment.special;

import com.probejs.document.comment.SpecialComment;
import lombok.Getter;

/**
 * class target
 */
@Getter
public class CommentTarget extends SpecialComment {

    public static final String MARK = "@target";
    private final String targetName;

    public CommentTarget(String line) {
        super(line);
        targetName = line.substring(MARK.length()+1);
    }
}
