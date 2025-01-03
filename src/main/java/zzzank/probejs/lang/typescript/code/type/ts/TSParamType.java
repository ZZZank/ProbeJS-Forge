package zzzank.probejs.lang.typescript.code.type.ts;

import zzzank.probejs.lang.typescript.Declaration;
import zzzank.probejs.lang.typescript.code.type.BaseType;
import zzzank.probejs.lang.typescript.refer.ImportInfos;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class TSParamType extends BaseType {
    public final BaseType baseType;
    public final List<BaseType> params;

    public TSParamType(BaseType baseType, Collection<? extends BaseType> params) {
        this.baseType = baseType;
        this.params = new ArrayList<>(params);
    }

    @Override
    public ImportInfos getImportInfos(@Nonnull FormatType type) {
        return ImportInfos.of(baseType.getImportInfos(type)).fromCodes(params, type);
    }

    @Override
    public String line(Declaration declaration, FormatType formatType) {
        return String.format(
            "%s<%s>",
            baseType.line(declaration, formatType),
            params.stream()
                .map(type -> type.line(declaration, formatType))
                .collect(Collectors.joining(", "))
        );
    }
}
