package zzzank.probejs.lang.typescript.code.type.js;

import lombok.EqualsAndHashCode;
import zzzank.probejs.lang.typescript.Declaration;
import zzzank.probejs.lang.typescript.code.type.BaseType;
import zzzank.probejs.lang.typescript.refer.ImportInfos;

import java.util.Collections;
import java.util.List;

@EqualsAndHashCode(callSuper = false)
public class JSPrimitiveType extends BaseType {

    public final String content;

    public JSPrimitiveType(String content) {
        this.content = content;
    }

    @Override
    public ImportInfos getImportInfos() {
        return ImportInfos.of();
    }

    @Override
    public List<String> format(Declaration declaration, FormatType input) {
        return Collections.singletonList(content);
    }
}
