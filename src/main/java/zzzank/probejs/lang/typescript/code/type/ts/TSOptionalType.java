package zzzank.probejs.lang.typescript.code.type.ts;

import zzzank.probejs.lang.typescript.Declaration;
import zzzank.probejs.lang.typescript.code.type.BaseType;
import zzzank.probejs.lang.typescript.refer.ImportInfos;

import javax.annotation.Nonnull;

public class TSOptionalType extends BaseType {
    public BaseType component;

    public TSOptionalType(BaseType component) {
        this.component = component;
    }

    @Override
    public String line(Declaration declaration, FormatType formatType) {
        return String.format("(%s)?", component.line(declaration, formatType));
    }

    @Override
    public ImportInfos getImportInfos(@Nonnull FormatType type) {
        return component.getImportInfos(type);
    }
}
