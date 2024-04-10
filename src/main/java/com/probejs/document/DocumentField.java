package com.probejs.document;

import com.probejs.document.parser.processor.IDocumentProvider;
import com.probejs.document.type.IType;
import com.probejs.document.type.DocTypeResolver;
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
        if (line.endsWith(";")) {
            line = line.substring(0, line.length() - 1);
        }

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
        this.name = parts.first().trim();
        this.type = DocTypeResolver.resolve(parts.second().trim());
        this.isFinal = f;
        this.isStatic = s;
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
    public List<String> format(int indent, int stepIndent) {
        List<String> formatted = new ArrayList<>();
        if (comment != null) {
            formatted.addAll(comment.format(indent, stepIndent));
        }
        formatted.add(
            String.format(
                "%s%s%s%s: %s;",
                PUtil.indent(indent),
                isStatic ? "static " : "",
                isFinal ? "readonly " : "",
                name,
                type.getTypeName()
            )
        );
        return formatted;
    }
}
