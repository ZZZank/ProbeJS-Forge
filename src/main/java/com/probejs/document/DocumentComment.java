package com.probejs.document;

import com.probejs.document.comment.AbstractComment;
import com.probejs.document.comment.CommentHandler;
import com.probejs.formatter.formatter.IFormatter;
import com.probejs.util.PUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class DocumentComment implements IDecorative, IFormatter {

    private final List<String> documentText;
    private final HashMap<Class<? extends AbstractComment>, List<AbstractComment>> abstractComments = new HashMap<>();

    public DocumentComment(List<String> documentText) {
        this.documentText = documentText.stream().map(String::trim).collect(Collectors.toList());
        this.documentText.stream()
            .map(t -> t.startsWith("*") ? t.substring(1).trim() : t)
            .filter(t -> CommentHandler.specialCommentHandler.containsKey(t.split(" ", 2)[0]))
            .map(t -> CommentHandler.specialCommentHandler.get(t.split(" ", 2)[0]).apply(t))
            .forEach(c -> abstractComments.computeIfAbsent(c.getClass(), s -> new ArrayList<>()).add(c));
    }

    public List<String> getRawDocumentText() {
        return documentText;
    }

    public List<AbstractComment> getSpecialCommentsList() {
        return abstractComments.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
    }

    public <T> List<T> getSpecialComments(Class<? extends T> clazz) {
        return abstractComments
            .getOrDefault(clazz, new ArrayList<>())
            .stream()
            .map(i -> (T) i)
            .collect(Collectors.toList());
    }

    public <T> T getSpecialComment(Class<? extends T> clazz, int index) {
        List<T> a = getSpecialComments(clazz);
        return a.size() <= index ? null : a.get(index);
    }

    public <T> T getSpecialComment(Class<? extends T> clazz) {
        return getSpecialComment(clazz, 0);
    }

    public List<String> getDocumentText() {
        return documentText
            .stream()
            .filter(text ->
                text.startsWith("*")
                    ? !CommentHandler.specialCommentHandler.containsKey(
                        text.substring(1).trim().split(" ", 2)[0]
                    )
                    : !CommentHandler.specialCommentHandler.containsKey(text.split(" ", 2)[0])
            )
            .collect(Collectors.toList());
    }

    @Override
    public List<String> format(int indent, int stepIndent) {
        return getDocumentText().stream().map(s -> PUtil.indent(indent) + s).collect(Collectors.toList());
    }
}
