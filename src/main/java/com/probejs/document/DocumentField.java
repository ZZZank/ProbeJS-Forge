package com.probejs.document;

import com.probejs.document.parser.processor.IDocumentProvider;
import com.probejs.document.type.DocType;
import com.probejs.document.type.DocTypeResolver;
import com.probejs.formatter.api.MultiFormatter;
import com.probejs.util.PUtil;
import com.probejs.util.Pair;
import com.probejs.util.StringUtil;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class DocumentField extends DocumentProperty implements IDocumentProvider<DocumentField>, MultiFormatter {

    private final boolean isFinal;
    private final boolean isStatic;
    @Getter
    private final String name;
    @Getter
    private final DocType type;

    public DocumentField(String line) {
        line = line.trim();
        if (line.endsWith(";")) {
            line = line.substring(0, line.length() - 1);
        }

        boolean f = false;
        boolean s = false;
        for (boolean flag = true; flag;) {
            if (line.startsWith("readonly ")) {
                line = line.substring(9).trim();
                f = true;
            } else if (line.startsWith("static ")) {
                line = line.substring(7).trim();
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

    @Override
    public DocumentField provide() {
        return this;
    }

    @Override
    public List<String> formatLines(int indent, int stepIndent) {
        List<String> formatted = new ArrayList<>();
        if (comment != null) {
            formatted.addAll(comment.formatLines(indent, stepIndent));
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
