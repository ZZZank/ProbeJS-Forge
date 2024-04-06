package com.probejs.document;

import com.probejs.document.comment.CommentUtil;
import com.probejs.formatter.formatter.IFormatter;
import com.probejs.util.PUtil;
import java.util.ArrayList;
import java.util.List;

public class DocumentClass implements IConcrete, IFormatter {

    private DocumentComment comment;
    private String name;
    private String superClass;
    private List<String> interfaces;
    private final List<DocumentField> fields = new ArrayList<>();
    private final List<DocumentMethod> methods = new ArrayList<>();

    public DocumentComment getComment() {
        return comment;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSuperClass(String superClass) {
        this.superClass = superClass;
    }

    public void setInterfaces(List<String> interfaces) {
        this.interfaces = interfaces;
    }

    public void acceptProperty(IDocument document) {
        if (document instanceof DocumentProperty) {
            DocumentComment comment = ((DocumentProperty) document).getComment();
            if (!CommentUtil.isLoaded(comment)) {
                return;
            }
        }

        if (document instanceof DocumentField) {
            fields.add((DocumentField) document);
        }
        if (document instanceof DocumentMethod) {
            methods.add((DocumentMethod) document);
        }
    }

    public void merge(DocumentClass other) {
        if (comment == null) {
            comment = other.getComment();
        }
        fields.addAll(other.getFields());
        methods.addAll(other.getMethods());
    }

    public List<DocumentField> getFields() {
        return fields;
    }

    public List<DocumentMethod> getMethods() {
        return methods;
    }

    public String getName() {
        return name;
    }

    @Override
    public void acceptDeco(List<IDecorative> decorates) {
        for (IDecorative decorative : decorates) {
            if (decorative instanceof DocumentComment) {
                this.comment = (DocumentComment) decorative;
            }
        }
    }

    @Override
    public List<String> format(int indent, int stepIndent) {
        List<String> formatted = new ArrayList<>();
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
        formatted.add(firstLine.append('{').toString());
        getFields().forEach(f -> formatted.addAll(f.format(indent + stepIndent, stepIndent)));
        getMethods().forEach(m -> formatted.addAll(m.format(indent + stepIndent, stepIndent)));
        formatted.add(PUtil.indent(indent) + "}");
        return formatted;
    }
}
