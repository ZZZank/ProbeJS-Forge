package com.probejs.document.comment.special;

import com.probejs.document.comment.SpecialComment;
import com.probejs.document.type.DocType;
import com.probejs.document.type.DocTypeResolver;
import lombok.Getter;

/**
 * type modify
 */
@Getter
public class CommentModify extends SpecialComment {

    public static final String MARK = "@modify";
    private final String name;
    private final DocType type;

    public CommentModify(String line) {
        super(line);
        String[] split = line.split(" ");
        // nameTo[0] is `@modify`
        this.name = split[1];
        this.type = DocTypeResolver.resolve(split[2]);
    }
}
