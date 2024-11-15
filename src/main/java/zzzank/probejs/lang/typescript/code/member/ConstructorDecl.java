package zzzank.probejs.lang.typescript.code.member;

import zzzank.probejs.lang.typescript.Declaration;
import zzzank.probejs.lang.typescript.code.type.BaseType;
import zzzank.probejs.lang.typescript.code.type.ts.TSVariableType;
import zzzank.probejs.lang.typescript.refer.ImportInfos;

import java.util.*;
import java.util.stream.Collectors;

public class ConstructorDecl extends CommentableCode {
    public final List<TSVariableType> variableTypes;
    public final List<ParamDecl> params;
    public String content = null;

    public ConstructorDecl(List<TSVariableType> variableTypes, List<ParamDecl> params) {
        this.variableTypes = variableTypes;
        this.params = params;
    }

    @Override
    public ImportInfos getImportInfos() {
        return ImportInfos.of()
            .fromCodes(variableTypes)
            .fromCodes(params.stream().map(p -> p.type));
    }

    @Override
    public List<String> formatRaw(Declaration declaration) {
        // Format head - constructor<T>
        String head = "constructor";
        if (!variableTypes.isEmpty()) {
            String variables = variableTypes.stream()
                    .map(type -> type.line(declaration, BaseType.FormatType.VARIABLE))
                    .collect(Collectors.joining(", "));
            head = String.format("%s<%s>", head, variables);
        }

        // Format body - (a: type, ...)
        String body = ParamDecl.formatParams(params, declaration);

        // Format tail - {/** content */}
        String tail = "";
        if (content != null) {
            tail = String.format("%s {/** %s */}", tail, content);
        }
        return Collections.singletonList(String.format("%s%s%s", head, body, tail));
    }
}
