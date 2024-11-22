package zzzank.probejs.lang.typescript.code.type.utility;

import lombok.AllArgsConstructor;
import zzzank.probejs.lang.typescript.Declaration;
import zzzank.probejs.lang.typescript.code.type.BaseType;
import zzzank.probejs.lang.typescript.refer.ImportInfo;
import zzzank.probejs.lang.typescript.refer.ImportInfos;

import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

@AllArgsConstructor
public class CustomType extends BaseType {
    public final BiFunction<Declaration, FormatType, String> formatter;
    public Function<FormatType, ImportInfos> imports;

    public CustomType(BiFunction<Declaration, FormatType, String> formatter, ImportInfo... imports) {
        this.formatter = formatter;
        this.imports = (type) -> ImportInfos.of(imports);
    }

    @Override
    public ImportInfos getImportInfos(FormatType type) {
        return imports.apply(type);
    }

    @Override
    public List<String> format(Declaration declaration, FormatType input) {
        return Collections.singletonList(formatter.apply(declaration, input));
    }
}
