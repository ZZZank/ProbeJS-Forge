package com.probejs.formatter.formatter;

import com.probejs.util.PUtil;
import java.util.ArrayList;
import java.util.List;

public class FormatterRawTS implements IFormatter {

    private final List<String> docs;

    public FormatterRawTS(List<String> docs) {
        this.docs = docs;
    }

    @Override
    public List<String> format(Integer indent, Integer stepIndent) {
        List<String> formatted = new ArrayList<>();
        formatted.add(PUtil.indent(indent) + "declare namespace TSDoc {");
        docs.stream().map(s -> PUtil.indent(indent + stepIndent) + s).forEach(formatted::add);
        formatted.add(PUtil.indent(indent) + "}");
        return formatted;
    }
}
