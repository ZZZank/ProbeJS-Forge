package zzzank.probejs.lang.typescript.code.member;

import lombok.val;
import zzzank.probejs.lang.typescript.Declaration;
import zzzank.probejs.lang.typescript.code.type.BaseType;
import zzzank.probejs.lang.typescript.refer.ImportInfos;

import java.util.Collections;
import java.util.List;

/**
 * Represents a type declaration. Standalone members are always exported.
 */
public class TypeDecl extends CommentableCode {
    public BaseType type;
    public final String symbol;

    public boolean exportDecl = true;

    public TypeDecl(String symbol, BaseType type) {
        this.symbol = symbol;
        this.type = type;
    }

    public TypeDecl setExport(boolean exportDecl) {
        this.exportDecl = exportDecl;
        return this;
    }

    @Override
    public ImportInfos getImportInfos() {
        return type.getImportInfos(BaseType.FormatType.INPUT);
    }

    @Override
    public List<String> formatRaw(Declaration declaration) {
        val format = exportDecl
            ? "export type %s = %s;"
            : "type %s = %s;";
        return Collections.singletonList(
            String.format(
                format,
                symbol,
                type.line(declaration, BaseType.FormatType.INPUT)
            )
        );
    }
}
