package com.probejs.document.comment.special;

import com.probejs.document.comment.SpecialComment;
import com.probejs.document.type.IDocType;
import com.probejs.document.type.DocTypeResolver;
import lombok.Getter;

@Getter
public class CommentReturns extends SpecialComment {

    private static final int MARK_LEN = "@returns ".length();
    private final IDocType returnType;

    public CommentReturns(String line) {
        super(line);
        returnType = DocTypeResolver.resolve(line.substring(MARK_LEN));
    }
}
