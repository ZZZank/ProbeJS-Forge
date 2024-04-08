package com.probejs.formatter.formatter;

import com.probejs.util.PUtil;
import java.util.ArrayList;
import java.util.List;

public class FormatterComments implements IFormatter {

    protected final List<String> raw;
    protected boolean isMultiLine;

    /**
     * construct a multi-line comment formatter, and formatted lines will be in
     * block comment style
     */
    public FormatterComments(List<String> lines) {
        this.raw = lines;
        this.isMultiLine = true;
    }

    /**
     * construct a multi-line comment formatter, and formatted lines will be in
     * block comment style
     */
    public FormatterComments(String... lines) {
        this.raw = new ArrayList<String>(lines.length);
        for (String line : lines) {
            this.raw.add(line);
        }
        this.isMultiLine = true;
    }

    /**
     * construct a single-line comment formatter, and formatted lines will be in
     * line comment style, e.g. <p>
     * by default the constructed formatter will use line style comment <p>
     * {@code // single line comment example}
     */
    public FormatterComments(String line) {
        this.raw = new ArrayList<>(1);
        this.raw.add(line);
        this.isMultiLine = false;
    }

    public boolean isMultiLine() {
        return this.isMultiLine;
    }

    public FormatterComments setMultiLine(boolean isMultiLine) {
        this.isMultiLine = isMultiLine;
        return this;
    }

    /**
     * replace all strings in it with a trimmed string(via {@code str.trim()})
     * @return formatter itself
     */
    public FormatterComments trim() {
        final int size = this.raw.size();
        for (int i = 0; i < size; i++) {
            this.raw.set(i, this.raw.get(i).trim());
        }
        return this;
    }

    @Override
    public List<String> format(int indent, int stepIndent) {
        final String idnt = PUtil.indent(indent);
        final String commentMark = this.isMultiLine ? " * " : "// ";
        List<String> lines = new ArrayList<>(2 + this.raw.size());
        if (this.isMultiLine) {
            lines.add(idnt + "/**");
        }
        for (final String line : this.raw) {
            lines.add(String.format("%s%s%s", idnt, commentMark, line));
        }
        if (this.isMultiLine) {
            lines.add(idnt + " */");
        }
        return lines;
    }
}
