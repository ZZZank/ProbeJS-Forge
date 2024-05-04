package com.probejs.document.comment.special;

import com.probejs.document.comment.AbstractComment;
import com.probejs.document.type.IType;
import com.probejs.document.type.DocTypeResolver;
import lombok.Getter;

@Getter
public class CommentAssign extends AbstractComment {

    private static final int MARK_LEN = "@assign ".length();
    private final IType type;

    public CommentAssign(String line) {
        super(line);
        type = DocTypeResolver.resolve(line.substring(MARK_LEN));
    }

}
