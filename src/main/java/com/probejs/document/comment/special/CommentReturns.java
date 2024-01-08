package com.probejs.document.comment.special;

import com.probejs.document.comment.AbstractComment;
import com.probejs.document.type.IType;
import com.probejs.document.type.Resolver;

public class CommentReturns extends AbstractComment {

    private static final int MARK_LEN = "@returns ".length();
    private final IType returnType;

    public CommentReturns(String line) {
        super(line);
        returnType = Resolver.resolveType(line.substring(MARK_LEN));
    }

    public IType getReturnType() {
        return returnType;
    }
}
