package com.probejs.formatter.formatter;

import com.probejs.util.PUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FormatterComments implements IFormatter {

    protected final List<String> raw;
    protected boolean isBlockStyle;

    /**
     * construct a multi-line comment formatter, and formatted lines will be in
     * block comment style
     */
    public FormatterComments(List<String> lines) {
        this.raw = lines;
        this.isBlockStyle = true;
    }

    /**
     * construct a multi-line comment formatter, and formatted lines will be in
     * block comment style
     */
    public FormatterComments(String... lines) {
        this.raw = new ArrayList<>(lines.length);
        this.raw.addAll(Arrays.asList(lines));
        this.isBlockStyle = true;
    }

    public boolean isBlockStyle() {
        return this.isBlockStyle;
    }

    public FormatterComments setBlockStyle(boolean isMultiLine) {
        this.isBlockStyle = isMultiLine;
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
        final String commentMark = this.isBlockStyle ? " * " : "// ";
        List<String> lines = new ArrayList<>(2 + this.raw.size());
        if (this.isBlockStyle) {
            lines.add(idnt + "/**");
        }
        for (final String line : this.raw) {
            lines.add(String.format("%s%s%s", idnt, commentMark, line));
        }
        if (this.isBlockStyle) {
            lines.add(idnt + " */");
        }
        return lines;
    }
}
