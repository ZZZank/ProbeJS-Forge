package com.probejs.formatter;

import com.probejs.formatter.api.MultiFormatter;
import com.probejs.util.PUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class FormatterRaw implements MultiFormatter {

    private final List<String> docs;
    /**
     * -- SETTER --
     *  if true, there will be two new lines as the begining and end of formatted strings
     *  that can mark the start and end of raw doc
     */
    @Setter
    @Getter
    private boolean commentMark;

    public FormatterRaw(List<String> docs) {
        this.docs = docs;
        this.commentMark = true;
    }

    public FormatterRaw(List<String> docs, boolean commentMark) {
        this.docs = docs;
        this.commentMark = commentMark;
    }

    @Override
    public List<String> formatLines(int indent, int stepIndent) {
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
