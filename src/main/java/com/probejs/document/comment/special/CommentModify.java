package com.probejs.document.comment.special;

import com.probejs.document.comment.AbstractComment;
import com.probejs.document.type.IType;
import com.probejs.document.type.TypeResolver;

public class CommentModify extends AbstractComment {

    private static final int MARK_LEN = "@modify ".length();
    private final String name;
    private final IType type;

    public CommentModify(String line) {
        super(line);
        String sub = line.substring(MARK_LEN);
        int idx = sub.indexOf(" ");
        name = sub.substring(0, idx).trim();
        type = TypeResolver.resolve(sub.substring(idx + 1));
    }

    public String getName() {
        return name;
    }

    public IType getType() {
        return type;
    }
}
