package com.probejs.rewrite.doc;

import com.probejs.document.comment.SpecialComment;

import java.util.ArrayList;
import java.util.List;

/**
 * only java doc comment, regular line comment / block comment are not supported by this
 */
public class DocComment {

    private final List<SpecialComment> specials;
    private final List<String> normals;

    /**
     * construct a new, empty DocComment
     */
    DocComment() {
        this.specials = new ArrayList<>(0);
        this.normals = new ArrayList<>(0);
    }

    DocComment(List<String> lines) {
        this();
        //TODO: parse
    }
}
