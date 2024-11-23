package zzzank.probejs.lang.typescript.code.member;

import zzzank.probejs.ProbeJS;
import zzzank.probejs.lang.typescript.Declaration;
import zzzank.probejs.lang.typescript.code.ts.VariableDeclaration;
import zzzank.probejs.lang.typescript.code.type.BaseType;
import zzzank.probejs.lang.typescript.refer.ImportInfos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class FieldDecl extends CommentableCode {
    public boolean isFinal = false;
    public boolean isStatic = false;
    public String name;
    public BaseType type;

    public FieldDecl(String name, BaseType type) {
        this.name = name;
        this.type = type;
    }

    public VariableDeclaration asVariableDecl() {
        return new VariableDeclaration(this.name, this.type);
    }

    @Override
    public ImportInfos getImportInfos() {
        return type.getImportInfos(BaseType.FormatType.RETURN);
    }

    @Override
    public List<String> formatRaw(Declaration declaration) {
        List<String> modifiers = new ArrayList<>();
        if (isStatic) {
            modifiers.add("static");
        }
        if (isFinal) {
            modifiers.add("readonly");
        }

        return Collections.singletonList(String.format("%s %s: %s",
            String.join(" ", modifiers),
            ProbeJS.GSON.toJson(name),
            type.line(declaration, BaseType.FormatType.RETURN)
        ));
    }
}
