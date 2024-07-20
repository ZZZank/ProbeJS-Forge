package zzzank.probejs.lang.typescript.code.member;

import zzzank.probejs.lang.typescript.Declaration;
import zzzank.probejs.lang.typescript.code.Code;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class CommentableCode extends Code {
    public final List<String> comments = new ArrayList<>();

    public List<String> formatComments() {
        List<String> formatted = new ArrayList<>();
        formatted.add("/**");
        for (String comment : comments) {
            formatted.add(String.format(" * %s",comment));
        }
        formatted.add(" */");
        return formatted;
    }

    public abstract List<String> formatRaw(Declaration declaration);

    public final List<String> format(Declaration declaration) {
        if (comments.isEmpty()) return formatRaw(declaration);
        List<String> result = new ArrayList<>(formatComments());
        result.addAll(formatRaw(declaration));
        return result;
    }

    public void addComment(String... comments) {
        for (String comment : comments) {
            this.comments.addAll(Arrays.asList(comment.split("\\n")));
        }
    }

    public void addCommentAtStart(String... comments) {
        List<String> lines = new ArrayList<>();
        for (String comment : comments) {
            lines.addAll(Arrays.asList(comment.split("\\n")));
        }
        this.comments.addAll(0, lines);
    }

    public void linebreak() {
        comments.add("");
    }

    public void newline(String... comments) {
        this.comments.add("");
        addComment(comments);
    }
}
