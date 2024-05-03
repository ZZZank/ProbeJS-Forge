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
import com.probejs.info.type.TypeInfoVariable;
import com.probejs.util.PUtil;
import com.probejs.util.StringUtil;
import lombok.Getter;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

public class FormatterMethod extends DocumentReceiver<DocumentMethod> implements IFormatter {

    @Getter
    private final MethodInfo info;
    private final Map<String, IType> paramModifiers;
    private IType returnModifiers;
    @Getter
    private final BeanType beanType;

    public FormatterMethod(MethodInfo methodInfo) {
        this.info = methodInfo;
        this.paramModifiers = new HashMap<>();
        this.returnModifiers = null;
        this.beanType = caculateBeanType(this.info);
    }

    public static enum BeanType {
        NONE,
        GETTER,
        SETTER,
        GETTER_IS,
    }

    public static BeanType caculateBeanType(MethodInfo info) {
        final String methodName = info.getName();
        if (
            methodName.length() < 4 &&
            (methodName.equals("is") || methodName.equals("get") || methodName.equals("set"))
        ) {
            return BeanType.NONE;
        }
        final int paramCount = info.getParams().size();

        if (
            paramCount == 0 &&
            methodName.startsWith("is") &&
            (
                //TODO: it seems that we can't pre-calculate the Boolean TypeInfoClass, why
                info.getReturnType().assignableFrom(new TypeInfoClass(Boolean.class)) ||
                info.getReturnType().assignableFrom(new TypeInfoClass(Boolean.TYPE))
            )
        ) {
            return BeanType.GETTER_IS;
        }
        if (paramCount == 0 && methodName.startsWith("get")) {
            return BeanType.GETTER;
        }
        if (paramCount == 1 && methodName.startsWith("set")) {
            return BeanType.SETTER;
        }
        return BeanType.NONE;
    }

    @Nullable
    public String getBeanedName() {
        switch (this.beanType) {
            case NONE:
                return null;
            case GETTER:
            case SETTER:
                return StringUtil.withLowerCaseHead(info.getName().substring(3));
            case GETTER_IS:
                return StringUtil.withLowerCaseHead(info.getName().substring(2));
        }
        return null;
    }

    public boolean isGetter() {
        return this.beanType == BeanType.GETTER || this.beanType == BeanType.GETTER_IS;
    }

    public String getBeanTypeString() {
        return isGetter()
            ? info.getReturnType().getTypeName()
            : info.getParams().get(0).getType().getTypeName();
    }

    public Map<String, IType> getParamModifiers() {
        if (this.paramModifiers.isEmpty()) {
            refreshModifiers();
        }
        return paramModifiers;
    }

    public IType getReturnModifiers() {
        if (this.returnModifiers == null) {
            refreshModifiers();
        }
        return returnModifiers;
    }

    private void refreshModifiers() {
        this.paramModifiers.clear();
        this.returnModifiers = null;
        if (document != null) {
            final DocumentComment comment = document.getComment();
            if (comment != null) {
                paramModifiers.putAll(CommentUtil.getTypeModifiers(comment));
                CommentReturns r = comment.getSpecialComment(CommentReturns.class);
                if (r != null) {
                    returnModifiers = r.getReturnType();
                }
            }
        }
    }

    private static String formatTypeParameterized(ITypeInfo info, boolean useSpecial) {
        if (!(info instanceof TypeInfoClass)) {
            return FormatterType.of(info, useSpecial).format();
        }
        final TypeInfoClass clazz = (TypeInfoClass) info;
        final StringBuilder sb = new StringBuilder(FormatterType.of(info, useSpecial).format());
        if (!NameResolver.isTypeSpecial(clazz.getResolvedClass()) && !clazz.getTypeVariables().isEmpty()) {
            sb.append('<');
            sb.append(String.join(", ", Collections.nCopies(clazz.getTypeVariables().size(), "any")));
            sb.append('>');
        }
        return sb.toString();
    }

    public String formatReturn() {
        IType returnModifier = getReturnModifiers();
        if (returnModifier != null) {
            return returnModifier.getTypeName();
        }
        return formatTypeParameterized(info.getReturnType(), false);
    }

    private String formatParam(MethodInfo.ParamInfo pInfo, boolean forceNoUnderscore) {
        ITypeInfo info = pInfo.getType();
        final Class<?> clazz = info.getResolvedClass();
        //No assigned types, and not enum, use normal route.
        if (!DocManager.typesAssignable.containsKey(clazz.getName()) && !clazz.isEnum()) {
            return formatTypeParameterized(info, true);
        }

        final StringBuilder sb = new StringBuilder();
        sb.append(
            FormatterType
                .of(info, false)
                .underscored((typeInfo) -> {
                    if (forceNoUnderscore) {
                        return false;
                    }
                    if (typeInfo instanceof TypeInfoClass) {
                        Class<?> c = typeInfo.getResolvedClass();
                        return !NameResolver.resolvedPrimitives.contains(c.getName());
                    }
                    return false;
                })
                .format()
        );
        if (info instanceof TypeInfoClass) {
            final TypeInfoClass cInfo = (TypeInfoClass) info;
            List<TypeInfoVariable> typeVariables = cInfo.getTypeVariables();
            if (!typeVariables.isEmpty()) {
                sb.append('<');
                sb.append(
                    typeVariables
                        .stream()
                        .map(ITypeInfo::getTypeName)
                        .map(NameResolver::getResolvedName)
                        .map(NameResolver.ResolvedName::getFullName)
                        .collect(Collectors.joining(","))
                );
                sb.append('>');
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
        Map<String, IType> modifiers = getParamModifiers();
        return info
            .getParams()
            .stream()
            .map(pInfo -> {
                String nameRaw = pInfo.getName();
                String paramType = modifiers.containsKey(nameRaw)
                    ? modifiers.get(nameRaw).transform(typeTransformer)
                    : formatParam(pInfo, forceNoUnderscore);
                return String.format(
                    "%s%s: %s",
                    pInfo.isVarArgs() ? "..." : "",
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
        if (!info.getTypeVariables().isEmpty()) {
            builder.append('<')
                .append(info.getTypeVariables().stream().map(ITypeInfo::getTypeName).collect(Collectors.joining(", ")))
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

        if (document != null) {
            DocumentComment comment = document.getComment();
            if (CommentUtil.isHidden(comment)) return lines;
            if (comment != null) lines.addAll(comment.format(indent, stepIndent));
        }

        String idnt = PUtil.indent(indent);
        String beaned = getBeanedName();
        if (this.beanType == BeanType.GETTER_IS) {
            lines.add(String.format("%sget %s(): boolean;", idnt, beaned));
        } else if (this.beanType == BeanType.GETTER) {
            lines.add(String.format("%sget %s(): %s;", idnt, beaned, formatReturn()));
        } else if (this.beanType == BeanType.SETTER) {
            lines.add(String.format("%sset %s(%s);", idnt, beaned, formatParams(Collections.emptyMap())));
        }
        return lines;
    }
}
