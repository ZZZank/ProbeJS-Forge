package com.probejs.document;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.probejs.document.comment.CommentHandler;
import com.probejs.document.comment.CommentUtil;
import com.probejs.document.comment.SpecialComment;
import com.probejs.formatter.FormatterComments;
import com.probejs.formatter.api.MultiFormatter;
import lombok.val;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DocumentComment implements IDecorative, MultiFormatter {

    private final List<String> normals;
    private final ListMultimap<Class<? extends SpecialComment>, SpecialComment> specials;

    public DocumentComment(List<String> lines) {
        int size = lines.size();
        if (size < 2 || !lines.get(0).startsWith("/*") || !lines.get(size - 1).endsWith("*/")) {
            throw new IllegalArgumentException("provided arg 'lines' is not valid block comment");
        }
        normals = new ArrayList<>();
        specials = ArrayListMultimap.create();

        Stream.of(
                Stream.of(CommentUtil.convertFirstLine(lines.get(0))),
                lines.subList(1, size - 1).stream(),
                Stream.of(CommentUtil.convertLastLine(lines.get(size - 1)))
            )
            .flatMap(Function.identity())
            .filter(Objects::nonNull)
            .map(CommentUtil::trimInnerLine)
            .forEach(line -> {
                val special = CommentHandler.tryParseSpecialComment(line);
                if (special != null) {
                    specials.put(special.getClass(), special);
                } else {
                    normals.add(line);
                }
            });
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
        return normals;
    }

    @Override
    public List<String> formatLines(int indent, int stepIndent) {
        if (this.normals.isEmpty()) {
            return Collections.emptyList();
        }
        return new FormatterComments(this.getNormalComments())
            .setStyle(FormatterComments.CommentStyle.J_DOC)
            .formatLines(indent, stepIndent);
    }
}
