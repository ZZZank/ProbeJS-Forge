package com.probejs.document.comment.special;

import com.probejs.document.comment.SpecialComment;
import com.probejs.document.type.DocType;
import com.probejs.document.type.DocTypeResolver;
import lombok.Getter;

@Getter
public class CommentAssign extends SpecialComment {

    public static final String MARK = "@assign";
    private final DocType type;

    public CommentAssign(String line) {
        super(line);
        type = DocTypeResolver.resolve(line.substring(MARK.length() + 1));
    }
}
