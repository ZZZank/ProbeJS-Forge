package zzzank.probejs.lang.typescript.code.type;

import zzzank.probejs.lang.typescript.Declaration;
import zzzank.probejs.lang.typescript.refer.ImportInfo;
import zzzank.probejs.lang.typescript.refer.ImportInfos;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;

public class CustomType extends BaseType {
    private final BiFunction<Declaration, FormatType, String> formatter;
    private final ImportInfo[] imports;

    public CustomType(BiFunction<Declaration, FormatType, String> formatter, ImportInfo[] imports) {
        this.formatter = formatter;
        this.imports = imports;
    }

    @Override
    public ImportInfos getImportInfos() {
        return ImportInfos.of(Arrays.asList(imports));
    }

    @Override
    public List<String> format(Declaration declaration, FormatType input) {
        return Collections.singletonList(formatter.apply(declaration, input));
    }
}
