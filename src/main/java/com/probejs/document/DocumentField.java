package com.probejs.document;

import com.probejs.document.parser.processor.IDocumentProvider;
import com.probejs.document.type.IType;
import com.probejs.document.type.Resolver;
import com.probejs.formatter.formatter.IFormatter;
import com.probejs.util.PUtil;
import com.probejs.util.Pair;
import com.probejs.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class DocumentField extends DocumentProperty implements IDocumentProvider<DocumentField>, IFormatter {

    private final boolean isFinal;
    private final boolean isStatic;
    private final String name;
    private final IType type;

    public DocumentField(String line) {
        line = line.trim();
        boolean f = false;
        boolean s = false;
        for (boolean flag = true; flag;) {
            if (line.startsWith("readonly")) {
                line = line.substring(8).trim();
                f = true;
            } else if (line.startsWith("static")) {
                line = line.substring(6).trim();
                s = true;
            } else {
                flag = false;
            }
        }
        Pair<String, String> parts = StringUtil.splitFirst(line, ":");
        name = parts.getFirst().trim();
        type = Resolver.resolveType(parts.getSecond().trim());

        isFinal = f;
        isStatic = s;
    }

    public boolean isFinal() {
        return isFinal;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public String getName() {
        return name;
    }

    public IType getType() {
        return type;
    }

    @Override
    public DocumentField provide() {
        return this;
    }

    @Override
    public List<String> format(Integer indent, Integer stepIndent) {
        List<String> formatted = new ArrayList<>();
        if (comment != null) {
            formatted.addAll(comment.format(indent, stepIndent));
        }
        List<String> pre = new ArrayList<>();
        if (isStatic) pre.add("static");
        if (isFinal) pre.add("readonly");
        pre.add(String.format("%s: %s;", name, type.getTypeName()));
        formatted.add(PUtil.indent(indent) + String.join(" ", pre));
        return formatted;
    }
}
