package com.probejs.rewrite.doc;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.probejs.document.comment.SpecialComment;
import dev.latvian.kubejs.util.UtilsJS;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * only java doc comment, regular line comment / block comment are not supported by this
 */
@Getter
public class DocComment {

    private final ListMultimap<Class<? extends SpecialComment>, SpecialComment> specials;
    private final List<String> normals;

    /**
     * construct a new, empty DocComment
     */
    DocComment() {
        this.specials = ArrayListMultimap.create(3,1);
        this.normals = new ArrayList<>(0);
    }

    public <T extends SpecialComment> List<T> getSpecialComment(Class<T> type) {
        return UtilsJS.cast(this.specials.get(type));
    }
}
