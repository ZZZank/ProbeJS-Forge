package com.probejs.formatter.formatter;

import com.probejs.document.DocumentComment;
import com.probejs.document.DocumentMethod;
import com.probejs.document.Manager;
import com.probejs.document.comment.CommentUtil;
import com.probejs.document.comment.special.CommentReturns;
import com.probejs.document.type.IType;
import com.probejs.document.type.TypeNamed;
import com.probejs.formatter.NameResolver;
import com.probejs.info.MethodInfo;
import com.probejs.info.type.ITypeInfo;
import com.probejs.info.type.TypeInfoClass;
import com.probejs.util.PUtil;
import com.probejs.util.Pair;
import java.util.*;
import java.util.stream.Collectors;

public class FormatterMethod extends DocumentReceiver<DocumentMethod> implements IFormatter {

    private final MethodInfo methodInfo;

    public FormatterMethod(MethodInfo methodInfo) {
        this.methodInfo = methodInfo;
    }

    public MethodInfo getMethodInfo() {
        return methodInfo;
    }

    public String getBean() {
        String methodName = methodInfo.getName();
        if (methodName.equals("is") || methodName.equals("get") || methodName.equals("set")) {
            return null;
        }
        int paramSize = methodInfo.getParams().size();
        if (
            methodName.startsWith("is") &&
            paramSize == 0 &&
            (
                methodInfo.getReturnType().assignableFrom(TypeInfoClass.BOOL_CLASS) ||
                methodInfo.getReturnType().assignableFrom(TypeInfoClass.BOOL_TYPE)
            )
        ) {
            return PUtil.getCamelCase(methodName.substring(2));
        }
        if (methodName.startsWith("get") && paramSize == 0) {
            return PUtil.getCamelCase(methodName.substring(3));
        }
        if (methodName.startsWith("set") && paramSize == 1) {
            return PUtil.getCamelCase(methodName.substring(3));
        }
        return null;
    }

    public boolean isGetter() {
        return !methodInfo.getName().startsWith("set");
    }

    public String getBeanTypeString() {
        return isGetter()
            ? methodInfo.getReturnType().getTypeName()
            : methodInfo.getParams().get(0).getType().getTypeName();
    }

    private Pair<Map<String, IType>, IType> getModifiers() {
        Map<String, IType> modifiers = new HashMap<>();
        IType returns = null;
        if (document != null) {
            DocumentComment comment = document.getComment();
            if (comment != null) {
                modifiers.putAll(CommentUtil.getTypeModifiers(comment));
                CommentReturns r = comment.getSpecialComment(CommentReturns.class);
                if (r != null) {
                    returns = r.getReturnType();
                }
            }
        }
        return new Pair<>(modifiers, returns);
    }

    private static String formatTypeParameterized(ITypeInfo info, boolean useSpecial) {
        if (!(info instanceof TypeInfoClass)) {
            return new FormatterType(info, useSpecial).format(0, 0);
        }
        TypeInfoClass clazz = (TypeInfoClass) info;
        StringBuilder sb = new StringBuilder(new FormatterType(info, useSpecial).format(0, 0));
        if (!NameResolver.isTypeSpecial(clazz.getResolvedClass()) && clazz.getTypeVariables().size() != 0) {
            sb.append(
                String.format(
                    "<%s>",
                    String.join(", ", Collections.nCopies(clazz.getTypeVariables().size(), "any"))
                )
            );
        }
        return sb.toString();
    }

    private String formatReturn() {
        return formatTypeParameterized(methodInfo.getReturnType(), false);
    }

    private String formatParamUnderscore(ITypeInfo info) {
        Class<?> resolvedClass = info.getResolvedClass();
        //No assigned types, and not enum, use normal route.
        if (!Manager.typesAssignable.containsKey(resolvedClass.getName()) && !resolvedClass.isEnum()) {
            return formatTypeParameterized(info, true);
        }

        StringBuilder sb = new StringBuilder(
            new FormatterType(
                info,
                false,
                (typeInfo, rawString) -> {
                    if (typeInfo instanceof TypeInfoClass) {
                        Class<?> clazz = typeInfo.getResolvedClass();
                        if (!NameResolver.resolvedPrimitives.contains(clazz.getName())) {
                            return (rawString + "_");
                        }
                    }
                    return rawString;
                }
            )
                .format(0, 0)
        );
        if (info instanceof TypeInfoClass) {
            TypeInfoClass classInfo = (TypeInfoClass) info;
            int typeCount = classInfo.getTypeVariables().size();
            if (typeCount != 0) {
                sb.append('<');
                sb.append(String.join(", ", Collections.nCopies(typeCount, "any")));
                sb.append('>');
            }
        }
        return sb.toString();
    }

    private String formatParams(Map<String, IType> modifiers, Map<String, String> renames) {
        return String.format(
            "(%s)",
            methodInfo
                .getParams()
                .stream()
                .map(paramInfo -> {
                    String paramType = modifiers.containsKey(paramInfo.getName())
                        ? modifiers
                            .get(paramInfo.getName())
                            .getTransformedName((t, s) -> {
                                if (!(t instanceof TypeNamed)) {
                                    return s;
                                }
                                TypeNamed n = (TypeNamed) t;
                                if (
                                    NameResolver.resolvedNames.containsKey(n.getRawTypeName()) &&
                                    !NameResolver.resolvedPrimitives.contains((n.getRawTypeName()))
                                ) {
                                    return s + "_";
                                }
                                return s;
                            })
                        : formatParamUnderscore(paramInfo.getType());
                    return String.format(
                        "%s: %s",
                        NameResolver.getNameSafe(
                            renames.getOrDefault(paramInfo.getName(), paramInfo.getName())
                        ),
                        paramType
                    );
                })
                .collect(Collectors.joining(", "))
        );
    }

    @Override
    public List<String> format(Integer indent, Integer stepIndent) {
        List<String> formatted = new ArrayList<>();

        if (document != null) {
            DocumentComment comment = document.getComment();
            if (CommentUtil.isHidden(comment)) {
                return formatted;
            }
            if (comment != null) {
                formatted.addAll(comment.format(indent, stepIndent));
            }
        }

        Pair<Map<String, IType>, IType> modifierPair = getModifiers();
        Map<String, IType> modifiers = modifierPair.getFirst();
        IType returnModifier = modifierPair.getSecond();
        Map<String, String> renames = new HashMap<>();
        if (document != null) {
            renames.putAll(CommentUtil.getRenames(document.getComment()));
        }

        StringBuilder builder = new StringBuilder();

        builder.append(PUtil.indent(indent));
        if (methodInfo.isStatic()) {
            builder.append("static ");
        }
        builder.append(methodInfo.getName());
        if (methodInfo.getTypeVariables().size() != 0) {
            builder.append('<');
            builder.append(
                methodInfo
                    .getTypeVariables()
                    .stream()
                    .map(ITypeInfo::getTypeName)
                    .collect(Collectors.joining(", "))
            );
            builder.append('>');
        }
        // param
        builder.append(formatParams(modifiers, renames));
        builder.append(": ");
        // return type
        builder.append(returnModifier != null ? returnModifier.getTypeName() : formatReturn());
        // end
        builder.append(';');

        formatted.add(builder.toString());
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
            if (CommentUtil.isHidden(comment)) return formatted;
            if (comment != null) formatted.addAll(comment.format(indent, stepIndent));
        }

        formatted.add(PUtil.indent(indent));
        if (methodName.startsWith("is")) {
            formatted.add(String.format("get %s(): boolean;", getBean()));
        } else if (methodName.startsWith("get")) {
            formatted.add(
                String.format(
                    "get %s(): %s;",
                    getBean(),
                    returnModifier == null ? formatReturn() : returnModifier.getTypeName()
                )
            );
        } else if (methodName.startsWith("set")) {
            MethodInfo.ParamInfo info = methodInfo.getParams().get(0);
            String name = info.getName();
            formatted.add(
                String.format("set %s%s;", getBean(), formatParams(paramModifiers, new HashMap<>()))
            );
        }
        return formatted;
    }
}
