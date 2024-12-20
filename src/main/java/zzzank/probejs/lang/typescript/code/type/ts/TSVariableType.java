package zzzank.probejs.lang.typescript.code.type.ts;

import lombok.val;
import org.jetbrains.annotations.Nullable;
import zzzank.probejs.lang.typescript.Declaration;
import zzzank.probejs.lang.typescript.code.type.BaseType;
import zzzank.probejs.lang.typescript.code.type.Types;
import zzzank.probejs.lang.typescript.refer.ImportInfos;

import javax.annotation.Nonnull;

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
    public ImportInfos getImportInfos(@Nonnull FormatType type) {
        val imports = ImportInfos.of();
        if (extendsType != null) {
            imports.addAll(extendsType.getImportInfos(type));
        }
        if (defaultTo != null) {
            imports.addAll(defaultTo.getImportInfos(type));
        }
        return imports;
    }

    @Override
    public String line(Declaration declaration, FormatType formatType) {
        val builder = new StringBuilder();
        //name
        builder.append(symbol);
        if (formatType == FormatType.VARIABLE) {
            if (extendsType != null) {
                builder.append(" extends ").append(extendsType.line(declaration, FormatType.RETURN));
            }
            if (defaultTo != null) {
                builder.append(" = ").append(defaultTo.format(declaration, FormatType.RETURN));
            }
        }
        return builder.toString();
    }
}
