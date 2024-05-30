package com.probejs.document.comment.special;

import com.probejs.document.comment.SpecialComment;
import lombok.Getter;

@Getter
public class CommentRename extends SpecialComment {

    public static final String MARK = "@rename";
    private final String name;
    private final String to;

    public CommentRename(String line) {
        super(line);
        String[] nameTo = line.split(" ");
        // nameTo[0] is `@rename`
        name = nameTo[1];
        to = nameTo[2];
    }
}
