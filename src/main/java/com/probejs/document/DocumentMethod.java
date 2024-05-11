package com.probejs.document;

import com.probejs.document.parser.processor.IDocumentProvider;
import com.probejs.document.type.IDocType;
import com.probejs.document.type.DocTypeResolver;
import com.probejs.formatter.api.MultiFormatter;
import com.probejs.info.clazz.MethodInfo;
import com.probejs.util.PUtil;
import com.probejs.util.StringUtil;
import lombok.Getter;
import lombok.val;

import java.util.*;
import java.util.stream.Collectors;

public class DocumentMethod
    extends DocumentProperty
    implements IDocumentProvider<DocumentMethod>, MultiFormatter {

    @Override
    public DocumentMethod provide() {
        return this;
    }

    @Override
    public List<String> formatLines(int indent, int stepIndent) {
        List<String> formatted = new ArrayList<>();
        if (comment != null) {
            formatted.addAll(comment.formatLines(indent, stepIndent));
        }
        val paramStr = getParams()
            .stream()
            .map(documentParam ->
                String.format(
                    "%s: %s",
                    documentParam.getName(),
                    documentParam.getType().transform(IDocType.defaultTransformer)
                )
            )
            .collect(Collectors.joining(", "));
        val sb = new StringBuilder(PUtil.indent(indent));
        if (isStatic) {
            sb.append("static ");
        }
        sb.append(String.format("%s(%s): %s;", name, paramStr, returnType.getTypeName()));
        formatted.add(sb.toString());
        return formatted;
    }

    @Getter
    public static class DocumentParam {

        private final String name;
        private final IDocType type;

        private DocumentParam(String name, IDocType type) {
            this.name = name;
            this.type = type;
        }
    }

    private final boolean isStatic;
    @Getter
    private final String name;
    @Getter
    private final IDocType returnType;
    @Getter
    private final List<DocumentParam> params;

    public DocumentMethod(String line) {
        line = line.trim();
        if (line.endsWith(";")) {
            line = line.substring(0, line.length() - 1);
        }
        // e.g. static fnName(a: (string|number), b: {required: bool}): FnReturnName
        if (line.startsWith("static ")) {
            line = line.substring(7).trim();
            isStatic = true;
        } else {
            isStatic = false;
        }
        // e.g. fnName(a: (string|number), b: {required: bool}): FnReturnName
        // e.g. at "fnName(", which is 6
        int nameIndex = line.indexOf("(");
        // e.g. at "fnName(a: (string|number), b: {required: bool}):"
        int methodIndex = StringUtil.indexLayer(line, ":");

        // e.g. "fnName"
        name = line.substring(0, nameIndex).trim();
        // e.g. buildParams("(a: (string|number), b: {required: bool})")
        params = buildParams(line.substring(nameIndex, methodIndex));
        // e.g. Resolver.resolveType(" FnReturnName"), no need to trim, resolveType() will do so
        returnType = DocTypeResolver.resolve(line.substring(methodIndex + 1));
    }

    /**
     *
     * @param paramsStr E.g. "(a: (string|number), b: {required: bool})"
     */
    private List<DocumentParam> buildParams(String paramsStr) {
        List<DocumentParam> paramList = new ArrayList<>();
        if (paramsStr.isEmpty()) {
            return paramList;
        }
        paramsStr = paramsStr.trim().substring(1, paramsStr.length() - 1).trim();
        if (paramsStr.isEmpty()) {
            return paramList;
        }
        for (String paramStr : StringUtil.splitLayer(paramsStr, ",")) {
            val nameAndType = paramStr.trim().split(":", 2);
            paramList.add(new DocumentParam(nameAndType[0].trim(), DocTypeResolver.resolve(nameAndType[1])));
        }
        return paramList;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public boolean testMethod(MethodInfo methodInfo) {
        if (methodInfo.isStatic() != isStatic || !Objects.equals(methodInfo.getName(), name)) {
            return false;
        }

        Map<String, MethodInfo.ParamInfo> params = new HashMap<>();
        Map<String, DocumentParam> docParams = new HashMap<>();
        methodInfo.getParams().forEach(p -> params.put(p.getName(), p));
        this.params.forEach(p -> docParams.put(p.name, p));

        if (
            !params.keySet().equals(docParams.keySet()) ||
            !DocTypeResolver.typeEquals(returnType, methodInfo.getType())
        ) {
            return false;
        }

        for (Map.Entry<String, MethodInfo.ParamInfo> e : params.entrySet()) {
            DocumentParam doc = docParams.get(e.getKey());
            if (!DocTypeResolver.typeEquals(doc.getType(), e.getValue().getType())) return false;
        }

        return true;
    }
}
