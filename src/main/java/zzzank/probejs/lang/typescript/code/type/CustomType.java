package zzzank.probejs.lang.typescript.code.type;

import zzzank.probejs.lang.typescript.Declaration;
import zzzank.probejs.lang.typescript.refer.ImportInfo;
import zzzank.probejs.lang.typescript.refer.ImportInfos;

import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class CustomType extends BaseType {
    private final BiFunction<Declaration, FormatType, String> formatter;
    public Supplier<ImportInfos> imports;

    public CustomType(BiFunction<Declaration, FormatType, String> formatter, Supplier<ImportInfos> imports) {
        this.formatter = formatter;
        this.imports = imports;
    }

    public CustomType(BiFunction<Declaration, FormatType, String> formatter, ImportInfo... imports) {
        this.formatter = formatter;
        this.imports = () -> ImportInfos.of(imports);
    }

    @Override
    public ImportInfos getImportInfos() {
        return imports.get();
    }

    @Override
    public List<String> format(Declaration declaration, FormatType input) {
        return Collections.singletonList(formatter.apply(declaration, input));
    }
}
