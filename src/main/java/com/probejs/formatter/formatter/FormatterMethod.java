package com.probejs.formatter.formatter;

import com.probejs.document.DocumentComment;
import com.probejs.document.DocumentMethod;
import com.probejs.document.Manager;
import com.probejs.document.comment.CommentUtil;
import com.probejs.document.comment.special.CommentReturns;
import com.probejs.document.type.IType;
import com.probejs.formatter.NameResolver;
import com.probejs.info.MethodInfo;
import com.probejs.info.type.ITypeInfo;
import com.probejs.info.type.TypeInfoClass;
import com.probejs.util.PUtil;
import com.probejs.util.Pair;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

public class FormatterMethod extends DocumentReceiver<DocumentMethod> implements IFormatter {

    private final MethodInfo methodInfo;
    private Pair<Map<String, IType>, IType> modifiersCache;

    public FormatterMethod(MethodInfo methodInfo) {
        this.methodInfo = methodInfo;
        modifiersCache = null;
    }

    public MethodInfo getMethodInfo() {
        return methodInfo;
    }

    @Nullable
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
                //TODO: it seems that we can't pre-calculate the Boolean TypeInfoClass, why
                methodInfo.getReturnType().assignableFrom(new TypeInfoClass(Boolean.class)) ||
                methodInfo.getReturnType().assignableFrom(new TypeInfoClass(Boolean.TYPE))
            )
        ) {
            return PUtil.withLowerCaseHead(methodName.substring(2));
        }
        if (methodName.startsWith("get") && paramSize == 0) {
            return PUtil.withLowerCaseHead(methodName.substring(3));
        }
        if (methodName.startsWith("set") && paramSize == 1) {
            return PUtil.withLowerCaseHead(methodName.substring(3));
        }
        return null;
    }

    /**
     * Warning: This method assumes that the method is knowned to be either getter or setter
     * @return True if method name is `isXXX` or `getXX`
     */
    public boolean isGetter() {
        return !methodInfo.getName().startsWith("set");
    }

    public String getBeanTypeString() {
        return isGetter()
            ? methodInfo.getReturnType().getTypeName()
            : methodInfo.getParams().get(0).getType().getTypeName();
    }

    public Pair<Map<String, IType>, IType> getModifiers() {
        if (modifiersCache != null) {
            return modifiersCache;
        }
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
        modifiersCache = new Pair<>(modifiers, returns);
        return modifiersCache;
    }

    private static String formatTypeParameterized(ITypeInfo info, boolean useSpecial) {
        if (!(info instanceof TypeInfoClass)) {
            return new FormatterType(info, useSpecial).format(0, 0);
        }
        TypeInfoClass clazz = (TypeInfoClass) info;
        StringBuilder sb = new StringBuilder(new FormatterType(info, useSpecial).format(0, 0));
        if (!NameResolver.isTypeSpecial(clazz.getResolvedClass()) && clazz.getTypeVariables().size() != 0) {
            sb.append('<');
            sb.append(String.join(", ", Collections.nCopies(clazz.getTypeVariables().size(), "any")));
            sb.append('>');
        }
        return sb.toString();
    }

    public String formatReturn() {
        IType returnModifier = getModifiers().getSecond();
        if (returnModifier != null) {
            return returnModifier.getTypeName();
        }
        return formatTypeParameterized(methodInfo.getReturnType(), false);
    }

    private String formatParamUnderscore(ITypeInfo info) {
        return formatParamUnderscore(info, false);
    }

    private String formatParamUnderscore(ITypeInfo info, boolean forceNoUnderscore) {
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
                    if (!forceNoUnderscore && typeInfo instanceof TypeInfoClass) {
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
                sb.append('<').append(String.join(", ", Collections.nCopies(typeCount, "any"))).append('>');
            }
        }
        return sb.toString();
    }

    /**
     * Get a `a: string, b: number` style String representation of params of this method
     */
    public String formatParams(Map<String, String> renames) {
        return formatParams(renames, false);
    }

    /**
     * Get a `a: string, b: number` style String representation of params of this method
     */
    public String formatParams(Map<String, String> renames, boolean forceNoUnderscore) {
        BiFunction<IType, String, String> typeTransformer = forceNoUnderscore
            ? IType.dummyTransformer
            : IType.underscoreTransformer;
        Map<String, IType> modifiers = getModifiers().getFirst();
        // modifiers = getModifiers().getFirst();
        return methodInfo
            .getParams()
            .stream()
            .map(paramInfo -> {
                String paramNameRaw = paramInfo.getName();
                String paramType = modifiers.containsKey(paramNameRaw)
                    ? modifiers.get(paramNameRaw).transform(typeTransformer)
                    : formatParamUnderscore(paramInfo.getType(), forceNoUnderscore);
                return String.format(
                    "%s: %s",
                    NameResolver.getNameSafe(renames.getOrDefault(paramNameRaw, paramNameRaw)),
                    paramType
                );
            })
            .collect(Collectors.joining(", "));
    }

    @Override
    public List<String> format(int indent, int stepIndent) {
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

        Map<String, String> renames = new HashMap<>();
        if (document != null) {
            renames.putAll(CommentUtil.getRenames(document.getComment()));
        }

        StringBuilder builder = new StringBuilder(PUtil.indent(indent));

        if (methodInfo.isStatic()) {
            builder.append("static ");
        }
        builder.append(methodInfo.getName());
        if (methodInfo.getTypeVariables().size() != 0) {
            builder
                .append('<')
                .append(
                    methodInfo
                        .getTypeVariables()
                        .stream()
                        .map(ITypeInfo::getTypeName)
                        .collect(Collectors.joining(", "))
                )
                .append('>');
        }
        builder
            // param
            .append('(')
            .append(formatParams(renames))
            .append("): ")
            // return type
            .append(formatReturn())
            // end
            .append(';');

        formatted.add(builder.toString());
        return formatted;
    }

    public List<String> formatBean(int indent, int stepIndent) {
        List<String> formatted = new ArrayList<>();

        String methodName = methodInfo.getName();
        Pair<Map<String, IType>, IType> modifierPair = getModifiers();
        // Map<String, IType> paramModifiers = modifierPair.getFirst();
        IType returnModifier = modifierPair.getSecond();

        if (document != null) {
            DocumentComment comment = document.getComment();
            if (CommentUtil.isHidden(comment)) return formatted;
            if (comment != null) formatted.addAll(comment.format(indent, stepIndent));
        }

        String idnt = PUtil.indent(indent);
        if (methodName.startsWith("is")) {
            formatted.add(idnt + String.format("get %s(): boolean;", getBean()));
        } else if (methodName.startsWith("get")) {
            formatted.add(
                idnt +
                String.format(
                    "get %s(): %s;",
                    getBean(),
                    returnModifier == null ? formatReturn() : returnModifier.getTypeName()
                )
            );
        } else if (methodName.startsWith("set")) {
            MethodInfo.ParamInfo info = methodInfo.getParams().get(0);
            String name = info.getName();
            formatted.add(idnt + String.format("set %s(%s);", getBean(), formatParams(new HashMap<>())));
        }
        return formatted;
    }
}
