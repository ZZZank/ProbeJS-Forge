package zzzank.probejs.lang.typescript.code.type.ts;

import zzzank.probejs.lang.typescript.Declaration;
import zzzank.probejs.lang.typescript.code.type.BaseType;
import zzzank.probejs.lang.typescript.refer.ImportInfos;

import javax.annotation.Nonnull;

public class TSArrayType extends BaseType {
    public BaseType component;

    public TSArrayType(BaseType component) {
        this.component = component;
    }

    @Override
    public ImportInfos getImportInfos(@Nonnull FormatType type) {
        return component.getImportInfos(type);
    }

    @Override
    public String line(Declaration declaration, FormatType formatType) {
        return "(" + component.line(declaration, formatType) + ")[]";
    }
}
