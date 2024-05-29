package com.probejs.document.comment.special;

import com.probejs.document.comment.SpecialComment;
import com.probejs.document.type.IDocType;
import com.probejs.document.type.DocTypeResolver;
import lombok.Getter;

/**
 * type modify
 */
@Getter
public class CommentModify extends SpecialComment {

    private static final int MARK_LEN = "@modify ".length();
    private final String name;
    private final IDocType type;

    public CommentModify(String line) {
        super(line);
        String[] split = line.split(" ");
        // nameTo[0] is `@modify`
        this.name = split[1];
        this.type = DocTypeResolver.resolve(split[2]);
    }
}
