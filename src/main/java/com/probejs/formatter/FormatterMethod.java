package com.probejs.formatter;

import com.probejs.document.DocManager;
import com.probejs.document.DocumentComment;
import com.probejs.document.DocumentMethod;
import com.probejs.document.comment.CommentUtil;
import com.probejs.document.comment.special.CommentReturns;
import com.probejs.document.type.IDocType;
import com.probejs.formatter.api.DocumentReceiver;
import com.probejs.formatter.api.MultiFormatter;
import com.probejs.formatter.resolver.NameResolver;
import com.probejs.info.clazz.MethodInfo;
import com.probejs.info.type.IType;
import com.probejs.info.type.TypeClass;
import com.probejs.util.PUtil;
import com.probejs.util.StringUtil;
import lombok.Getter;
import lombok.val;

import java.util.*;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

public class FormatterMethod extends DocumentReceiver<DocumentMethod> implements MultiFormatter {

    @Getter
    private final MethodInfo info;
    private final Map<String, IDocType> paramModifiers;
    private IDocType returnModifiers;
    @Getter
    private final BeanType beanType;

    public FormatterMethod(MethodInfo methodInfo) {
        this.info = methodInfo;
        this.paramModifiers = new HashMap<>();
        this.returnModifiers = null;
        this.beanType = caculateBeanType(this.info);
    }

    public enum BeanType {
        NONE,
        GETTER,
        SETTER,
        GETTER_IS,
    }

    public static BeanType caculateBeanType(MethodInfo info) {
        val methodName = info.getName();
        if (
            methodName.length() < 4 &&
            (methodName.equals("is") || methodName.equals("get") || methodName.equals("set"))
        ) {
            return BeanType.NONE;
        }
        val paramCount = info.getParams().size();

        if (
            paramCount == 0 &&
            methodName.startsWith("is") &&
            (
                //TODO: it seems that we can't pre-calculate the Boolean TypeClass, why
                info.getType().assignableFrom(new TypeClass(Boolean.class)) ||
                info.getType().assignableFrom(new TypeClass(Boolean.TYPE))
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
        return switch (this.beanType) {
            case NONE -> null;
            case GETTER, SETTER -> StringUtil.withLowerCaseHead(info.getName().substring(3));
            case GETTER_IS -> StringUtil.withLowerCaseHead(info.getName().substring(2));
        };
    }

    public boolean isGetter() {
        return this.beanType == BeanType.GETTER || this.beanType == BeanType.GETTER_IS;
    }

    public String getBeanTypeString() {
        return isGetter()
            ? info.getType().getTypeName()
            : info.getParams().get(0).getType().getTypeName();
    }

    public Map<String, IDocType> getParamModifiers() {
        if (this.paramModifiers.isEmpty()) {
            refreshModifiers();
        }
        return paramModifiers;
    }

    public IDocType getReturnModifiers() {
        if (this.returnModifiers == null) {
            refreshModifiers();
        }
        return returnModifiers;
    }

    private void refreshModifiers() {
        this.paramModifiers.clear();
        this.returnModifiers = null;
        if (document != null) {
            val comment = document.getComment();
            if (comment != null) {
                paramModifiers.putAll(CommentUtil.getTypeModifiers(comment));
                val r = comment.getSpecialComment(CommentReturns.class);
                if (r != null) {
                    returnModifiers = r.getReturnType();
                }
            }
        }
    }

    public String formatReturn() {
        val returnModifier = getReturnModifiers();
        if (returnModifier != null) {
            return returnModifier.getTypeName();
        }
        return FormatterType.of(info.getType()).underscored(false).format();
    }

    private String formatParam(MethodInfo.ParamInfo pInfo, boolean forceNoUnderscore) {
        val info = pInfo.getType();
        val clazz = info.getResolvedClass();
        return FormatterType.of(pInfo.getType(), false)
            .underscored(type -> {
                if (forceNoUnderscore) {
                    return false;
                }
                val base = type.getBase();
                if (base instanceof TypeClass) {
                    return DocManager.typesAssignable.containsKey(clazz.getName())
                        && !NameResolver.resolvedPrimitives.contains(base.getResolvedClass().getName());
                }
                return false;
            })
            .format();
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
        val typeTransformer = forceNoUnderscore
            ? IDocType.dummyTransformer
            : IDocType.defaultTransformer;
        val modifiers = getParamModifiers();
        return info
            .getParams()
            .stream()
            .map(pInfo -> {
                val nameRaw = pInfo.getName();
                val paramType = modifiers.containsKey(nameRaw)
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
    public List<String> formatLines(int indent, int stepIndent) {
        List<String> formatted = new ArrayList<>();

        if (document != null) {
            DocumentComment comment = document.getComment();
            if (CommentUtil.isHidden(comment)) {
                return formatted;
            }
            if (comment != null) {
                formatted.addAll(comment.formatLines(indent, stepIndent));
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
                .append(info.getTypeVariables().stream().map(IType::getTypeName).collect(Collectors.joining(", ")))
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
            if (comment != null) lines.addAll(comment.formatLines(indent, stepIndent));
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
