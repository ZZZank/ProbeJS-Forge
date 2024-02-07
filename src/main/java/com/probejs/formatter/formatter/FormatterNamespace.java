package com.probejs.formatter.formatter;

import com.probejs.util.PUtil;
import java.util.ArrayList;
import java.util.List;

public class FormatterNamespace implements IFormatter {

    private final String path;
    private final List<? extends IFormatter> formatters;

    public FormatterNamespace(String path, List<? extends IFormatter> formatters) {
        this.path = path;
        this.formatters = formatters;
    }

    @Override
    public List<String> format(Integer indent, Integer stepIndent) {
        List<String> formatted = new ArrayList<>();
        formatted.add(PUtil.indent(indent) + String.format("declare namespace %s {", path));
        for (IFormatter formatter : formatters) {
            formatted.addAll(formatter.format(indent + stepIndent, stepIndent));
        }
        formatted.add(PUtil.indent(indent) + "}");
        return formatted;
    }
}
