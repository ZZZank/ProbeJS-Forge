package com.probejs.document;

import com.probejs.document.parser.processor.IDocumentProvider;
import com.probejs.document.type.IType;
import com.probejs.document.type.TypeResolver;
import com.probejs.formatter.formatter.IFormatter;
import com.probejs.info.MethodInfo;
import com.probejs.util.PUtil;
import com.probejs.util.StringUtil;
import java.util.*;
import java.util.stream.Collectors;

public class DocumentMethod
    extends DocumentProperty
    implements IDocumentProvider<DocumentMethod>, IFormatter {

    @Override
    public DocumentMethod provide() {
        return this;
    }

    public String getMethodBody() {
        StringBuilder sb = new StringBuilder();
        if (isStatic) {
            sb.append("static ");
        }
        return sb.append(name).append("(%s): ").append(returnType.getTypeName()).append(";").toString();
    }

    @Override
    public List<String> format(int indent, int stepIndent) {
        List<String> formatted = new ArrayList<>();
        if (comment != null) formatted.addAll(comment.format(indent, stepIndent));
        String paramStr = getParams()
            .stream()
            .map(documentParam ->
                String.format(
                    "%s: %s",
                    documentParam.getName(),
                    documentParam.getType().transform(IType.defaultTransformer)
                )
            )
            .collect(Collectors.joining(", "));
        formatted.add(PUtil.indent(indent) + String.format(getMethodBody(), paramStr));
        return formatted;
    }

    private static class DocumentParam {

        private final String name;
        private final IType type;

        private DocumentParam(String name, IType type) {
            this.name = name;
            this.type = type;
        }

        public IType getType() {
            return type;
        }

        public String getName() {
            return name;
        }
    }

    private final boolean isStatic;
    private final String name;
    private final IType returnType;
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
        returnType = TypeResolver.resolve(line.substring(methodIndex + 1));
    }

    /**
     *
     * @param paramsStr E.g. "(a: (string|number), b: {required: bool})"
     * @return
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
            String[] nameAndType = paramStr.trim().split(":", 2);
            paramList.add(new DocumentParam(nameAndType[0].trim(), TypeResolver.resolve(nameAndType[1])));
        }
        return paramList;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public String getName() {
        return name;
    }

    public IType getReturnType() {
        return returnType;
    }

    public List<DocumentParam> getParams() {
        return params;
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
            !TypeResolver.typeEquals(returnType, methodInfo.getReturnType())
        ) {
            return false;
        }

        for (Map.Entry<String, MethodInfo.ParamInfo> e : params.entrySet()) {
            DocumentParam doc = docParams.get(e.getKey());
            if (!TypeResolver.typeEquals(doc.getType(), e.getValue().getType())) return false;
        }

        return true;
    }
}
