package com.probejs.formatter.formatter;

import com.probejs.util.PUtil;
import java.util.ArrayList;
import java.util.List;

public class FormatterRaw implements IFormatter {

    private final List<String> docs;
    private boolean commentMark;

    public FormatterRaw(List<String> docs) {
        this.docs = docs;
        this.commentMark = true;
    }

    public FormatterRaw(List<String> docs, boolean commentMark) {
        this.docs = docs;
        this.commentMark = commentMark;
    }

    public boolean isCommentMark() {
        return commentMark;
    }

    /**
     * if true, there will be two new lines as the begining and end of formatted strings 
     * that can mark the start and end of raw doc
     */
    public void setCommentMark(boolean commentMark) {
        this.commentMark = commentMark;
    }

    @Override
    public List<String> format(int indent, int stepIndent) {
        List<String> lines = new ArrayList<>();
        String idnt = PUtil.indent(indent);
        if (commentMark) {
            lines.add(idnt + "// Raw TS doc region start");
        }
        for (String line : docs) {
            lines.add(idnt + line);
        }
        if (commentMark) {
            lines.add(idnt + "// Raw TS doc region end");
        }
        return lines;
    }
}
