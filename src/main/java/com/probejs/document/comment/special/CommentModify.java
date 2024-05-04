package com.probejs.document.comment.special;

import com.probejs.document.comment.AbstractComment;
import com.probejs.document.type.IType;
import com.probejs.document.type.DocTypeResolver;
import lombok.Getter;

@Getter
public class CommentModify extends AbstractComment {

    private static final int MARK_LEN = "@modify ".length();
    private final String name;
    private final IType type;

    public CommentModify(String line) {
        super(line);
        String[] split = line.split(" ");
        // nameTo[0] is `@modify`
        this.name = split[1];
        this.type = DocTypeResolver.resolve(split[2]);
    }

}
