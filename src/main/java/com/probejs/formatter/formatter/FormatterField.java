package com.probejs.formatter.formatter;

import com.probejs.document.DocumentComment;
import com.probejs.document.DocumentField;
import com.probejs.document.comment.special.CommentHidden;
import com.probejs.formatter.NameResolver;
import com.probejs.info.FieldInfo;
import com.probejs.info.type.InfoTypeResolver;
import com.probejs.util.PUtil;
import java.util.ArrayList;
import java.util.List;

public class FormatterField extends DocumentReceiver<DocumentField> implements IFormatter {

    private final FieldInfo fieldInfo;
    private boolean isInterface = false;

    public FormatterField(FieldInfo fieldInfo) {
        this.fieldInfo = fieldInfo;
    }

    public void setInterface(boolean anInterface) {
        isInterface = anInterface;
    }

    @Override
    public List<String> format(Integer indent, Integer stepIndent) {
        List<String> formatted = new ArrayList<>();
        DocumentComment comment = document != null ? document.getComment() : null;
        if (comment != null) {
            if (comment.getSpecialComment(CommentHidden.class) != null) {
                return formatted;
            }
            formatted.addAll(comment.format(indent, stepIndent));
        }

        StringBuilder builder = new StringBuilder(PUtil.indent(indent));
        if (fieldInfo.isStatic() && !isInterface) {
            builder.append("static ");
        }
        if (fieldInfo.isFinal()) {
            builder.append("readonly ");
        }
        builder.append(fieldInfo.getName());
        builder.append(": ");

        if (document != null) {
            builder.append(document.getType().getTypeName());
        } else if (fieldInfo.isStatic() && NameResolver.formatValue(fieldInfo.getStaticValue()) != null) {
            builder.append(NameResolver.formatValue(fieldInfo.getStaticValue()));
        } else {
            builder.append(
                new FormatterType(
                    fieldInfo.getType(),
                    NameResolver.specialTypeGuards.getOrDefault(
                        InfoTypeResolver.getContainedTypeOrSelf(fieldInfo.getType()).getResolvedClass(),
                        true
                    )
                )
                    .format(0, 0)
            );
        }
        builder.append(';');
        formatted.add(builder.toString());
        return formatted;
    }

    public FieldInfo getFieldInfo() {
        return fieldInfo;
    }
}
