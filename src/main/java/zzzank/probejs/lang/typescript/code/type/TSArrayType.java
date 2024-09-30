package zzzank.probejs.lang.typescript.code.type;

import zzzank.probejs.lang.typescript.Declaration;
import zzzank.probejs.lang.typescript.refer.ImportInfo;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class TSArrayType extends BaseType {
    public BaseType component;

    public TSArrayType(BaseType component) {
        this.component = component;
    }

    @Override
    public Collection<ImportInfo> getImportInfos() {
        return component.getImportInfos();
    }

    @Override
    public List<String> format(Declaration declaration, FormatType input) {
        return Collections.singletonList(String.format("(%s)[]",component.line(declaration, input)));
    }
}
