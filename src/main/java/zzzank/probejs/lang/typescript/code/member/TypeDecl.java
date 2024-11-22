package zzzank.probejs.lang.typescript.code.member;

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

    public TypeDecl(String symbol, BaseType type) {
        this.symbol = symbol;
        this.type = type;
    }

    @Override
    public ImportInfos getImportInfos() {
        return type.getImportInfos(null);
    }

    @Override
    public List<String> formatRaw(Declaration declaration) {
        return Collections.singletonList(
            String.format("export type %s = %s;", symbol, type.line(declaration, BaseType.FormatType.INPUT))
        );
    }
}
