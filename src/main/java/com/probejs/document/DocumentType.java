package com.probejs.document;

import com.probejs.document.comment.CommentUtil;
import com.probejs.document.parser.processor.IDocumentProvider;
import com.probejs.document.type.IType;
import com.probejs.document.type.DocTypeResolver;
import com.probejs.formatter.formatter.IFormatter;
import com.probejs.util.PUtil;
import com.probejs.util.Pair;
import com.probejs.util.StringUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DocumentType implements IDocumentProvider<DocumentType>, IFormatter, IConcrete {

    //type <name> = <type>;

    private DocumentComment comment;
    private final String name;
    private final IType type;

    public DocumentType(String name, IType type) {
        this.name = name;
        this.type = type;
    }

    public static DocumentType of(String line) {
        line = line.trim().substring("type ".length()).trim();
        if (line.endsWith(";")) {
            line = line.substring(0, line.length() - 1);
        }
        Pair<String, String> nameType = StringUtil.splitFirst(line, "=");
        return new DocumentType(nameType.first().trim(), DocTypeResolver.resolve(nameType.second().trim()));
    }

    @Override
    public DocumentType provide() {
        return this;
    }

    @Override
    public List<String> format(int indent, int stepIndent) {
        if (!CommentUtil.isLoaded(comment) || CommentUtil.isHidden(comment)) {
            return new ArrayList<>(0);
        }
        return Collections.singletonList(
            String.format(
                "%stype %s = %s;",
                PUtil.indent(indent),
                name,
                IType.defaultTransformer.apply(type, type.getTypeName())
            )
        );
    }

    @Override
    public void acceptDeco(List<IDecorative> decorates) {
        decorates.forEach(d -> {
            if (d instanceof DocumentComment) {
                comment = (DocumentComment) d;
            }
        });
    }

    public IType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public DocumentComment getComment() {
        return comment;
    }
}
