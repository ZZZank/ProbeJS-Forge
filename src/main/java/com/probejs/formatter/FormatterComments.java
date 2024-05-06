package com.probejs.formatter;

import com.probejs.formatter.api.MultiFormatter;
import com.probejs.util.PUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FormatterComments implements MultiFormatter {

    @AllArgsConstructor
    @Getter
    public enum CommentStyle {
        J_DOC("/**", " * ", " */"),
        BLOCK("/*", " * ", " */"),
        LINE(null, "// ", null);

        @Nullable
        private final String begin;
        @Nonnull
        private final String inline;
        @Nullable
        private final String end;
    }

    private final List<String> raw;
    @Getter
    private CommentStyle style;

    /**
     * construct a multi-line comment formatter, and formatted lines will be in
     * block comment style
     */
    public FormatterComments(List<String> lines) {
        this.raw = lines;
        this.style = CommentStyle.BLOCK;
    }

    /**
     * construct a multi-line comment formatter, and formatted lines will be in
     * block comment style
     */
    public FormatterComments(String... lines) {
        this(Arrays.asList(lines));
    }

    public FormatterComments setStyle(CommentStyle style) {
        this.style = style;
        return this;
    }

    /**
     * replace all strings in it with a trimmed string(via {@code str.trim()})
     * @return formatter itself
     */
    public FormatterComments trim() {
        val size = this.raw.size();
        for (int i = 0; i < size; i++) {
            this.raw.set(i, this.raw.get(i).trim());
        }
        return this;
    }

    @Override
    public List<String> formatLines(int indent, int stepIndent) {
        val idnt = PUtil.indent(indent);
        val lines = new ArrayList<String>(2 + this.raw.size());
        if (this.style.begin != null) {
            lines.add(idnt + this.style.begin);
        }
        for (final String line : this.raw) {
            lines.add(String.format("%s%s%s", idnt, this.style.inline, line));
        }
        if (this.style.end != null) {
            lines.add(idnt + this.style.end);
        }
        return lines;
    }
}
