package com.probejs.formatter;

import com.probejs.formatter.api.IFormatter;
import com.probejs.formatter.api.MultiFormatter;
import com.probejs.util.PUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class FormatterNamespace implements MultiFormatter {

    private final String path;
    private final Collection<? extends IFormatter> formatters;

    public FormatterNamespace(String path, Collection<? extends IFormatter> formatters) {
        this.path = path;
        this.formatters = formatters;
    }

    public FormatterNamespace(String path, IFormatter ...formatters) {
        this.path = path;
        this.formatters = Arrays.asList(formatters);
    }

    @Override
    public List<String> formatLines(int indent, int stepIndent) {
        List<String> lines = new ArrayList<>();
        lines.add(PUtil.indent(indent) + String.format("declare namespace %s {", path));
        for (IFormatter formatter : formatters) {
            lines.addAll(formatter.formatLines(indent + stepIndent, stepIndent));
        }
        lines.add(PUtil.indent(indent) + "}");
        return lines;
    }
}
