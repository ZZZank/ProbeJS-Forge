package zzzank.probejs.lang.typescript.code.type.utility;

import zzzank.probejs.lang.typescript.Declaration;
import zzzank.probejs.lang.typescript.code.type.BaseType;
import zzzank.probejs.lang.typescript.refer.ImportInfos;

import java.util.List;

/**
 * @author ZZZank
 */
public class ImportShield<T extends BaseType> extends BaseType {
    public final T inner;
    private final ImportInfos imports;

    public ImportShield(T inner, ImportInfos imports) {
        this.inner = inner;
        this.imports = imports;
    }

    @Override
    public ImportInfos getImportInfos() {
        return imports == null ? inner.getImportInfos() : imports;
    }

    @Override
    public List<String> format(Declaration declaration, FormatType formatType) {
        return inner.format(declaration, formatType);
    }
}
