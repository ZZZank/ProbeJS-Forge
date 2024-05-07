package com.probejs.document.comment.special;

import com.probejs.document.comment.SpecialComment;
import com.probejs.document.type.IDocType;
import com.probejs.document.type.DocTypeResolver;
import lombok.Getter;

@Getter
public class CommentAssign extends SpecialComment {

    private static final int MARK_LEN = "@assign ".length();
    private final IDocType type;

    public CommentAssign(String line) {
        super(line);
        type = DocTypeResolver.resolve(line.substring(MARK_LEN));
    }
}
