package com.probejs.document;

import com.probejs.document.comment.CommentUtil;
import com.probejs.document.parser.processor.IDocumentProvider;
import com.probejs.document.type.DocType;
import com.probejs.document.type.DocTypeResolver;
import com.probejs.formatter.api.MultiFormatter;
import com.probejs.util.PUtil;
import com.probejs.util.StringUtil;
import lombok.Getter;
import lombok.val;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * {@code type <name> = <type>;}
 */
@Getter
public class DocTypeAssign extends DocumentProperty implements IDocumentProvider<DocTypeAssign>, MultiFormatter {

    private final String name;
    private final DocType type;

    public DocTypeAssign(String name, DocType type) {
        this.name = name;
        this.type = type;
    }

    public static DocTypeAssign of(String line) {
        line = line.trim().substring("type ".length()).trim();
        if (line.endsWith(";")) {
            line = line.substring(0, line.length() - 1);
        }
        val nameType = StringUtil.splitFirst(line, "=");
        return new DocTypeAssign(nameType.first().trim(), DocTypeResolver.resolve(nameType.second().trim()));
    }

    @Override
    public DocTypeAssign provide() {
        return this;
    }

    @Override
    public List<String> formatLines(int indent, int stepIndent) {
        if (!CommentUtil.isLoaded(comment) || CommentUtil.isHidden(comment)) {
            return Collections.emptyList();
        }
        val lines = new ArrayList<String>(1);
        lines.addAll(comment.formatLines(0, 4));
        lines.add(String.format("%stype %s = %s;",
            PUtil.indent(indent),
            name,
            type.transform(DocType.defaultTransformer)
        ));
        return lines;
    }
}
