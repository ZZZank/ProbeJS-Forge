package com.probejs.rewrite.doc;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.probejs.document.comment.CommentHandler;
import com.probejs.document.comment.SpecialComment;
import com.probejs.util.Pair;
import lombok.Getter;
import lombok.val;

import java.util.ArrayList;
import java.util.Collection;
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

    DocComment(List<String> lines) {
        this();
        //TODO: parse
    }

    /**
     * @return {trimmed line, special comment}
     */
    private static Pair<String, SpecialComment> parseFirstLine(String line) {
        line = line.trim();
        if (!line.startsWith("/**")) {
            throw new IllegalArgumentException("arg not first line of a javadoc comment");
        }
        line = line.substring(3);
        return new Pair<>(line, CommentHandler.tryParseSpecialComment(line));
    }

    private static Pair<String, SpecialComment> parseLastLine(String line) {
        line = line.trim();
        if (!line.endsWith("*/")) {
            throw new IllegalArgumentException("arg not last line of a javadoc comment");
        }
        //TODO
        return null;
    }

    public <T extends SpecialComment> void acceptSpecials(Collection<T> specials) {
        for (T special : specials) {
            this.specials.put(special.getClass(), special);
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends SpecialComment> List<T> getSpecialComment(Class<T> type) {
        val comments = this.specials.get(type);
        val special = new ArrayList<T>(comments.size());
        for (val comment : comments) {
            special.add((T) comment);
        }
        return special;
    }
}
