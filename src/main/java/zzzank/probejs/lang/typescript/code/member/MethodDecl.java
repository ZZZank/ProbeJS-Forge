package zzzank.probejs.lang.typescript.code.member;

import zzzank.probejs.ProbeJS;
import zzzank.probejs.lang.typescript.Declaration;
import zzzank.probejs.lang.typescript.code.ts.FunctionDeclaration;
import zzzank.probejs.lang.typescript.code.type.BaseType;
import zzzank.probejs.lang.typescript.code.type.ts.TSVariableType;
import zzzank.probejs.lang.typescript.refer.ImportInfos;

import java.util.*;
import java.util.stream.Collectors;

public class MethodDecl extends CommentableCode {
    public String name;
    public boolean isAbstract = false;
    public boolean isStatic = false;
    public boolean isInterface = false;
    public List<TSVariableType> variableTypes;
    public List<ParamDecl> params;
    public BaseType returnType;
    public String content = null;


    public MethodDecl(String name, List<TSVariableType> variableTypes, List<ParamDecl> params, BaseType returnType) {
        this.name = name;
        this.variableTypes = new ArrayList<>(variableTypes);
        this.params = new ArrayList<>(params);
        this.returnType = returnType;
    }

    public FunctionDeclaration asFunctionDecl() {
        return new FunctionDeclaration(
            this.name,
            this.variableTypes,
            this.params,
            this.returnType
        );
    }

    @Override
    public ImportInfos getImportInfos() {
        return ImportInfos.of(returnType.getImportInfos(BaseType.FormatType.RETURN))
            .fromCodes(variableTypes, BaseType.FormatType.VARIABLE)
            .fromCodes(params.stream().map(p -> p.type), BaseType.FormatType.INPUT);
    }

    @Override
    public List<String> formatRaw(Declaration declaration) {
        // Format head - public static "name"<T, U extends A>
        List<String> modifiers = new ArrayList<>();
        if (!isInterface) modifiers.add("public");
        if (isStatic) modifiers.add("static");

        String head = String.join(" ", modifiers);
        head = String.format("%s %s", head, ProbeJS.GSON.toJson(name));
        if (!variableTypes.isEmpty()) {
            String variables = variableTypes.stream()
                .map(type -> type.line(declaration, BaseType.FormatType.VARIABLE))
                .collect(Collectors.joining(", "));
            head = String.format("%s<%s>", head, variables);
        }

        // Format body - (a: type, ...)
        String body = ParamDecl.formatParams(params, declaration);

        // Format tail - : returnType {/** content */}
        String tail = String.format(": %s", returnType.line(declaration, BaseType.FormatType.RETURN));
        if (content != null) {
            tail = String.format("%s {/** %s */}", tail, content);
        }

        return Collections.singletonList(String.format("%s%s%s", head, body, tail));
    }
}
