package com.probejs.formatter.formatter;

import com.probejs.document.DocManager;
import com.probejs.document.DocumentComment;
import com.probejs.document.DocumentMethod;
import com.probejs.document.comment.CommentUtil;
import com.probejs.document.comment.special.CommentReturns;
import com.probejs.document.type.IType;
import com.probejs.formatter.NameResolver;
import com.probejs.info.MethodInfo;
import com.probejs.info.type.ITypeInfo;
import com.probejs.info.type.TypeInfoClass;
import com.probejs.util.PUtil;
import com.probejs.util.Pair;
import com.probejs.util.StringUtil;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

public class FormatterMethod extends DocumentReceiver<DocumentMethod> implements IFormatter {

    private final MethodInfo info;
    private Pair<Map<String, IType>, IType> modifiersCache;

    public FormatterMethod(MethodInfo methodInfo) {
        this.info = methodInfo;
        modifiersCache = null;
    }

    public MethodInfo getInfo() {
        return info;
    }

    @Nullable
    public String getBean() {
        final String methodName = info.getName();
        if (methodName.equals("is") || methodName.equals("get") || methodName.equals("set")) {
            return null;
        }
        final int paramSize = info.getParams().size();
        if (
            methodName.startsWith("is") &&
            paramSize == 0 &&
            (
                //TODO: it seems that we can't pre-calculate the Boolean TypeInfoClass, why
                info.getReturnType().assignableFrom(new TypeInfoClass(Boolean.class)) ||
                info.getReturnType().assignableFrom(new TypeInfoClass(Boolean.TYPE))
            )
        ) {
            return StringUtil.withLowerCaseHead(methodName.substring(2));
        }
        if (methodName.startsWith("get") && paramSize == 0) {
            return StringUtil.withLowerCaseHead(methodName.substring(3));
        }
        if (methodName.startsWith("set") && paramSize == 1) {
            return StringUtil.withLowerCaseHead(methodName.substring(3));
        }
        return null;
    }

    /**
     * Warning: This method assumes that the method is knowned to be either getter or setter
     * @return True if method name is `isXXX` or `getXX`
     */
    public boolean isGetter() {
        return !info.getName().startsWith("set");
    }

    public String getBeanTypeString() {
        return isGetter()
            ? info.getReturnType().getTypeName()
            : info.getParams().get(0).getType().getTypeName();
    }

    public Pair<Map<String, IType>, IType> getModifiers() {
        if (modifiersCache != null) {
            return modifiersCache;
        }
        final Map<String, IType> modifiers = new HashMap<>();
        IType returns = null;
        if (document != null) {
            final DocumentComment comment = document.getComment();
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
        final TypeInfoClass clazz = (TypeInfoClass) info;
        final StringBuilder sb = new StringBuilder(new FormatterType(info, useSpecial).format(0, 0));
        if (!NameResolver.isTypeSpecial(clazz.getResolvedClass()) && clazz.getTypeVariables().size() != 0) {
            sb.append('<');
            sb.append(String.join(", ", Collections.nCopies(clazz.getTypeVariables().size(), "any")));
            sb.append('>');
        }
        return sb.toString();
    }

    public String formatReturn() {
        IType returnModifier = getModifiers().second;
        if (returnModifier != null) {
            return returnModifier.getTypeName();
        }
        return formatTypeParameterized(info.getReturnType(), false);
    }

    private String formatParamUnderscore(ITypeInfo info) {
        return formatParamUnderscore(info, false);
    }

    private String formatParamUnderscore(ITypeInfo info, boolean forceNoUnderscore) {
        final Class<?> resolvedClass = info.getResolvedClass();
        //No assigned types, and not enum, use normal route.
        if (!DocManager.typesAssignable.containsKey(resolvedClass.getName()) && !resolvedClass.isEnum()) {
            return formatTypeParameterized(info, true);
        }

        final StringBuilder sb = new StringBuilder();
        sb.append(
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
            final TypeInfoClass classInfo = (TypeInfoClass) info;
            final int typeCount = classInfo.getTypeVariables().size();
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
        final BiFunction<IType, String, String> typeTransformer = forceNoUnderscore
            ? IType.dummyTransformer
            : IType.defaultTransformer;
        Map<String, IType> modifiers = getModifiers().first;
        // modifiers = getModifiers().getFirst();
        return info
            .getParams()
            .stream()
            .map(pInfo -> {
                String nameRaw = pInfo.getName();
                String paramType = modifiers.containsKey(nameRaw)
                    ? modifiers.get(nameRaw).transform(typeTransformer)
                    : formatParamUnderscore(pInfo.getType(), forceNoUnderscore);
                return String.format(
                    "%s: %s",
                    NameResolver.getNameSafe(renames.getOrDefault(nameRaw, nameRaw)),
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

        if (info.isStatic()) {
            builder.append("static ");
        }
        builder.append(info.getName());
        if (info.getTypeVariables().size() != 0) {
            builder
                .append('<')
                .append(
                    info
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
        List<String> lines = new ArrayList<>();

        String methodName = info.getName();
        // Pair<Map<String, IType>, IType> modifierPair = getModifiers();
        // Map<String, IType> paramModifiers = modifierPair.getFirst();
        // IType returnModifier = modifierPair.getSecond();

        if (document != null) {
            DocumentComment comment = document.getComment();
            if (CommentUtil.isHidden(comment)) return lines;
            if (comment != null) lines.addAll(comment.format(indent, stepIndent));
        }

        String idnt = PUtil.indent(indent);
        String beaned = getBean();
        if (methodName.startsWith("is")) {
            lines.add(idnt + String.format("get %s(): boolean;", beaned));
        } else if (methodName.startsWith("get")) {
            lines.add(idnt + String.format("get %s(): %s;", beaned, formatReturn()));
        } else if (methodName.startsWith("set")) {
            lines.add(idnt + String.format("set %s(%s);", beaned, formatParams(new HashMap<>())));
        }
        return lines;
    }
}
