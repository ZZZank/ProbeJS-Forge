package com.probejs.document.comment.special;

import com.probejs.document.comment.AbstractComment;
import lombok.Getter;

@Getter
public class CommentRename extends AbstractComment {

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
