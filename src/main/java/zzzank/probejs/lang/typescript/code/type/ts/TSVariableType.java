package zzzank.probejs.lang.typescript.code.type.ts;

import lombok.val;
import org.jetbrains.annotations.Nullable;
import zzzank.probejs.lang.typescript.Declaration;
import zzzank.probejs.lang.typescript.code.type.BaseType;
import zzzank.probejs.lang.typescript.code.type.Types;
import zzzank.probejs.lang.typescript.refer.ImportInfos;

import java.util.Collections;
import java.util.List;

public class TSVariableType extends BaseType {
    public final String symbol;
    public final BaseType extendsType;
    public final BaseType defaultTo;

    public TSVariableType(String symbol, @Nullable BaseType extendsType, BaseType defaultTo) {
        this.symbol = symbol;
        this.extendsType = extendsType == Types.ANY ? null : extendsType;
        this.defaultTo = defaultTo == Types.ANY ? null : defaultTo;
    }

    @Override
    public ImportInfos getImportInfos() {
        val imports = ImportInfos.of();
        if (extendsType != null) {
            imports.addAll(extendsType.getImportInfos());
        }
        if (defaultTo != null) {
            imports.addAll(defaultTo.getImportInfos());
        }
        return imports;
    }

    @Override
    public List<String> format(Declaration declaration, FormatType formatType) {
        val name = switch (formatType) {
            case INPUT, RETURN -> symbol;
            case VARIABLE -> extendsType == null
                ? symbol
                : String.format("%s extends %s", symbol, extendsType.line(declaration, FormatType.RETURN));
        };
        return Collections.singletonList(defaultTo == null
            ? name
            : name + " = " + defaultTo.format(declaration, formatType)
        );
    }
}
