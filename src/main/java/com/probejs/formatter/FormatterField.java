package com.probejs.formatter;

import com.probejs.document.DocField;
import com.probejs.document.comment.special.CommentHidden;
import com.probejs.formatter.api.DocumentReceiver;
import com.probejs.formatter.api.MultiFormatter;
import com.probejs.formatter.resolver.PathResolver;
import com.probejs.info.clazz.FieldInfo;
import com.probejs.info.type.TypeResolver;
import com.probejs.util.PUtil;
import lombok.Getter;
import lombok.val;

import java.util.ArrayList;
import java.util.List;

public class FormatterField extends DocumentReceiver<DocField> implements MultiFormatter {

    @Getter
    private final FieldInfo info;
    private final boolean isFromInterface;

    public FormatterField(FieldInfo info) {
        this.info = info;
        this.isFromInterface = info.getFrom().isInterface();
    }

    @Override
    public List<String> formatLines(int indent, int stepIndent) {
        List<String> lines = new ArrayList<>();
        val comment = document != null ? document.getComment() : null;
        if (comment != null) {
            if (comment.getSpecialComment(CommentHidden.class) != null) {
                return lines;
            }
            lines.addAll(comment.formatLines(indent, stepIndent));
        }

        val builder = new StringBuilder(PUtil.indent(indent));
        if (info.isStatic() && !isFromInterface) {
            builder.append("static ");
        }
        if (info.isFinal()) {
            builder.append("readonly ");
        }
        builder.append(info.getName());
        builder.append(": ");

        if (document != null) {
            builder.append(document.getType().getTypeName());
        } else if (info.isStatic() && PathResolver.formatValue(info.getStaticValue()) != null) {
            builder.append(PathResolver.formatValue(info.getStaticValue()));
        } else {
            builder.append(
                FormatterType.of(
                    info.getType(),
                    PathResolver.specialTypeGuards.getOrDefault(
                        TypeResolver.getContainedTypeOrSelf(info.getType()).getResolvedClass(),
                        true
                    )
                )
                    .format()
            );
        }
        builder.append(';');
        lines.add(builder.toString());
        return lines;
    }
}
