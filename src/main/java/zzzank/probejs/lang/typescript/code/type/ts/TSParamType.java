package zzzank.probejs.lang.typescript.code.type.ts;

import zzzank.probejs.lang.typescript.Declaration;
import zzzank.probejs.lang.typescript.code.type.BaseType;
import zzzank.probejs.lang.typescript.refer.ImportInfos;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

public class TSParamType extends BaseType {
    public final BaseType baseType;
    public final List<BaseType> params;

    public TSParamType(BaseType baseType, List<? extends BaseType> params) {
        this.baseType = baseType;
        this.params = new ArrayList<>(params);
    }

    @Override
    public ImportInfos getImportInfos(@Nonnull FormatType type) {
        return ImportInfos.of(baseType.getImportInfos(type)).fromCodes(params, type);
    }

    @Override
    public List<String> format(Declaration declaration, FormatType input) {
        return Collections.singletonList(
            String.format(
                "%s<%s>",
                baseType.line(declaration, input),
                params.stream()
                    .map(type -> String.format("(%s)", type.line(declaration, input)))
                    .collect(Collectors.joining(", "))
            )
        );
    }
}
