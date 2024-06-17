package com.probejs.document;

import com.probejs.document.comment.CommentUtil;
import com.probejs.formatter.api.MultiFormatter;
import com.probejs.util.PUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


public class DocClass extends DocumentProperty implements IConcrete, MultiFormatter {

    @Setter
    @Getter
    private String name;
    @Setter
    private String superClass;
    @Setter
    private List<String> interfaces;
    @Getter
    private final List<DocField> fieldDocs = new ArrayList<>();
    @Getter
    private final List<DocMethod> methodDocs = new ArrayList<>();

    public void acceptProperty(IDocument document) {
        if (document instanceof DocumentProperty) {
            DocComment comment = ((DocumentProperty) document).getComment();
            if (!CommentUtil.isLoaded(comment)) {
                return;
            }
        }

        if (document instanceof DocField) {
            fieldDocs.add((DocField) document);
        }
        if (document instanceof DocMethod) {
            methodDocs.add((DocMethod) document);
        }
    }

    public void merge(DocClass other) {
        if (comment == null) {
            comment = other.getComment();
        }
        fieldDocs.addAll(other.getFieldDocs());
        methodDocs.addAll(other.getMethodDocs());
    }

    @Override
    public List<String> formatLines(int indent, int stepIndent) {
        List<String> lines = new ArrayList<>();
        StringBuilder firstLine = new StringBuilder(PUtil.indent(indent))
            .append("class ")
            .append(this.name)
            .append(' ');
        if (this.superClass != null) {
            firstLine.append("extends ").append(superClass).append(' ');
        }
        if (this.interfaces != null && !this.interfaces.isEmpty()) {
            firstLine.append("implements ").append(String.join(", ", this.interfaces)).append(' ');
        }
        lines.add(firstLine.append('{').toString());
        this.fieldDocs.forEach(f -> lines.addAll(f.formatLines(indent + stepIndent, stepIndent)));
        this.methodDocs.forEach(m -> lines.addAll(m.formatLines(indent + stepIndent, stepIndent)));
        lines.add(PUtil.indent(indent) + "}");
        return lines;
    }
}
