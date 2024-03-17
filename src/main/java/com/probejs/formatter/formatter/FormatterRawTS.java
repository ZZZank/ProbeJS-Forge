package com.probejs.formatter.formatter;

import com.probejs.util.PUtil;
import java.util.ArrayList;
import java.util.List;

public class FormatterRawTS implements IFormatter {

    private final List<String> docs;
    private boolean commentMark;

    public boolean willCommentMark() {
        return commentMark;
    }

    /**
     * if true, there will be two new lines as the begining and end of formatted strings 
     * that can mark the start and end of raw doc
     */
    public void setCommentMark(boolean commentMark) {
        this.commentMark = commentMark;
    }

    public FormatterRawTS(List<String> docs) {
        this.docs = docs;
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
