package com.probejs.document.comment.special;

import com.probejs.document.comment.AbstractComment;

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

    public String getName() {
        return name;
    }

    public String getTo() {
        return to;
    }
}
