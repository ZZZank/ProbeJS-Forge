package com.probejs.document;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.probejs.document.comment.CommentHandler;
import com.probejs.document.comment.CommentUtil;
import com.probejs.document.comment.SpecialComment;
import com.probejs.formatter.FormatterComments;
import com.probejs.formatter.api.MultiFormatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class DocumentComment implements IDecorative, MultiFormatter {

    private final List<String> rawLines;
    private final ListMultimap<Class<? extends SpecialComment>, SpecialComment> specials = ArrayListMultimap.create();

    public DocumentComment(List<String> lines) {
        int begin = 0;
        int end = lines.size();
        if (end < 2 || !lines.get(0).startsWith("/*") || !lines.get(end - 1).endsWith("*/")) {
            throw new IllegalArgumentException("provided arg 'lines' is not valid block comment");
        }
        //remove "/*" and "/**" at the front
        String tmp = lines.get(0).substring("/*".length());
        tmp = tmp.startsWith("*") ? tmp.substring(1) : tmp;
        tmp = tmp.trim();
        if (tmp.isEmpty()) {
            begin += 1;
        } else {
            lines.set(begin, tmp);
        }
        //remove "*/" at the end
        tmp = lines.get(end - 1);
        tmp = tmp.substring(0, tmp.length() - 2).trim();
        if (tmp.isEmpty()) {
            end -= 1;
        } else {
            lines.set(end - 1, tmp);
        }
        //remove "*"
        this.rawLines = lines.subList(begin, end)
            .stream()
            .map(CommentUtil::removeStarMark)
            .map(String::trim)
            .collect(Collectors.toList());
        this.rawLines.stream()
            .map(CommentHandler::tryParseSpecialComment)
            .filter(Objects::nonNull)
            .forEach(c -> specials.put(c.getClass(), c));
    }


    public List<SpecialComment> getSpecialCommentsList() {
        return new ArrayList<>(specials.values());
    }

    @SuppressWarnings("unchecked")
    public <T extends SpecialComment> List<T> getSpecialComments(Class<? extends T> clazz) {
        return specials
            .get(clazz)
            .stream()
            .map(i -> (T) i)
            .collect(Collectors.toList());
    }

    public <T extends SpecialComment> T getSpecialComment(Class<? extends T> clazz, int index) {
        List<T> a = getSpecialComments(clazz);
        return a.size() <= index ? null : a.get(index);
    }

    public <T extends SpecialComment> T getSpecialComment(Class<? extends T> clazz) {
        return getSpecialComment(clazz, 0);
    }

    public List<String> getNormalComments() {
        return rawLines
            .stream()
            .filter(text -> !CommentHandler.isCommentLineSpecial(text))
            .collect(Collectors.toList());
    }

    @Override
    public List<String> formatLines(int indent, int stepIndent) {
        if (this.rawLines.isEmpty()) {
            return Collections.emptyList();
        }
        return new FormatterComments(this.getNormalComments())
            .setStyle(FormatterComments.CommentStyle.J_DOC)
            .formatLines(indent, stepIndent);
    }
}
