package com.prunoideae.probejs.formatter.formatter;

import com.prunoideae.probejs.document.DocumentComment;
import com.prunoideae.probejs.document.DocumentMethod;
import com.prunoideae.probejs.document.comment.CommentUtil;
import com.prunoideae.probejs.document.comment.special.CommentReturns;
import com.prunoideae.probejs.document.type.IType;
import com.prunoideae.probejs.formatter.NameResolver;
import com.prunoideae.probejs.info.MethodInfo;
import com.prunoideae.probejs.info.TypeInfo;
import com.prunoideae.probejs.util.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class FormatterMethod extends DocumentedFormatter<DocumentMethod> implements IFormatter {
    private final MethodInfo methodInfo;

    private static String getCamelCase(String text) {
        return Character.toLowerCase(text.charAt(0)) + text.substring(1);
    }

    public FormatterMethod(MethodInfo methodInfo) {
        this.methodInfo = methodInfo;
    }

    public MethodInfo getMethodInfo() {
        return methodInfo;
    }


    public String getBean() {
        String methodName = methodInfo.getName();
        if (methodName.equals("is") || methodName.equals("get") || methodName.equals("set"))
            return null;
        if (methodName.startsWith("is") && methodInfo.getParams().size() == 0 && methodInfo.getReturnType().getRawType().equals(Boolean.class))
            return getCamelCase(methodName.substring(2));
        if (methodName.startsWith("get") && methodInfo.getParams().size() == 0)
            return getCamelCase(methodName.substring(3));
        if (methodName.startsWith("set") && methodInfo.getParams().size() == 1)
            return getCamelCase(methodName.substring(3));
        return null;
    }

    private Pair<Map<String, IType>, IType> getModifiers() {
        Map<String, IType> modifiers = new HashMap<>();
        IType returns = null;
        if (document != null) {
            DocumentComment comment = document.getComment();
            if (comment != null) {
                modifiers.putAll(CommentUtil.getTypeModifiers(comment));
                CommentReturns r = (CommentReturns) comment.getSpecialComment(CommentReturns.class);
                if (r != null)
                    returns = r.getReturnType();
            }
        }
        return new Pair<>(modifiers, returns);
    }

    private String formatReturn() {
        return new FormatterType(methodInfo.getReturnType()).format(0, 0);
    }

    private String formatParams(Map<String, IType> modifiers) {
        List<MethodInfo.ParamInfo> params = methodInfo.getParams();
        List<String> paramStrings = new ArrayList<>();
        for (MethodInfo.ParamInfo param : params) {
            if (modifiers.containsKey(param.getName()))
                paramStrings.add("%s: %s".formatted(NameResolver.getNameSafe(param.getName()), modifiers.get(param.getName()).getTypeName()));
            else
                paramStrings.add("%s: %s".formatted(NameResolver.getNameSafe(param.getName()), new FormatterType(param.getType()).format(0, 0)));
        }
        return String.join(", ", paramStrings);
    }

    @Override
    public List<String> format(Integer indent, Integer stepIndent) {
        List<String> formatted = new ArrayList<>();
        Pair<Map<String, IType>, IType> modifierPair = getModifiers();
        Map<String, IType> paramModifiers = modifierPair.getFirst();
        IType returnModifier = modifierPair.getSecond();
        if (document != null) {
            DocumentComment comment = document.getComment();
            if (CommentUtil.isHidden(comment))
                return formatted;
            if (comment != null)
                formatted.addAll(comment.format(indent, stepIndent));
        }

        StringBuilder sb = new StringBuilder();
        if (methodInfo.isStatic())
            sb.append("static ");
        sb.append(methodInfo.getName());
        if (methodInfo.getTypeVariables().size() != 0)
            sb.append("<%s>".formatted(methodInfo.getTypeVariables().stream().map(TypeInfo::getTypeName).collect(Collectors.joining(", "))));
        sb.append("(%s)".formatted(formatParams(paramModifiers)));
        sb.append(": %s".formatted(returnModifier == null ? formatReturn() : returnModifier.getTypeName()));

        formatted.add(" ".repeat(indent) + sb);
        return formatted;
    }

    public List<String> formatBean(Integer indent, Integer stepIndent) {
        List<String> formatted = new ArrayList<>();

        String methodName = methodInfo.getName();
        Pair<Map<String, IType>, IType> modifierPair = getModifiers();
        Map<String, IType> paramModifiers = modifierPair.getFirst();
        IType returnModifier = modifierPair.getSecond();

        if (document != null) {
            DocumentComment comment = document.getComment();
            if (CommentUtil.isHidden(comment))
                return formatted;
            if (comment != null)
                formatted.addAll(comment.format(indent, stepIndent));
        }

        if (methodName.startsWith("is"))
            formatted.add(" ".repeat(indent) + "get %s(): boolean;".formatted(getBean()));
        if (methodName.startsWith("get"))
            formatted.add(" ".repeat(indent) + "get %s(): %s;".formatted(getBean(), returnModifier == null ? formatReturn() : returnModifier.getTypeName()));
        if (methodName.startsWith("set"))
            formatted.add(" ".repeat(indent) + "set %s(%s);".formatted(getBean(), formatParams(paramModifiers)));
        return formatted;
    }
}
