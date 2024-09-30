package zzzank.probejs.lang.typescript.code.type;

import org.jetbrains.annotations.Nullable;
import zzzank.probejs.lang.typescript.Declaration;
import zzzank.probejs.lang.typescript.refer.ImportInfos;

import java.util.Collections;
import java.util.List;

public class TSVariableType extends BaseType {
    public final String symbol;
    public BaseType extendsType;

    public TSVariableType(String symbol, @Nullable BaseType extendsType) {
        this.symbol = symbol;
        this.extendsType = extendsType == Types.ANY ? null : extendsType;
    }

    @Override
    public ImportInfos getImportInfos() {
        return extendsType == null ? ImportInfos.ofImmutableEmpty() : extendsType.getImportInfos();
    }

    @Override
    public List<String> format(Declaration declaration, FormatType input) {
        return Collections.singletonList(switch (input) {
            case INPUT, RETURN -> symbol;
            case VARIABLE -> extendsType == null
                ? symbol
                : String.format("%s extends %s", symbol, extendsType.line(declaration, FormatType.RETURN));
        });
    }
}
