package com.probejs.document.comment.special;

import com.probejs.document.comment.SpecialComment;
import com.probejs.document.type.DocType;
import com.probejs.document.type.DocTypeResolver;
import lombok.Getter;

@Getter
public class CommentReturns extends SpecialComment {

    public static final String MARK = "@returns";
    private final DocType returnType;

    public CommentReturns(String line) {
        super(line);
        returnType = DocTypeResolver.resolve(line.substring(MARK.length()+1));
    }
}
