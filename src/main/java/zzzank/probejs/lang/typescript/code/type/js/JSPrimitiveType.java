package zzzank.probejs.lang.typescript.code.type.js;

import zzzank.probejs.lang.typescript.Declaration;
import zzzank.probejs.lang.typescript.code.type.BaseType;
import zzzank.probejs.lang.typescript.refer.ImportInfo;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class JSPrimitiveType extends BaseType {

    public final String content;

    public JSPrimitiveType(String content) {
        this.content = content;
    }


    @Override
    public Collection<ImportInfo> getImportInfos() {
        return Collections.emptyList();
    }

    @Override
    public List<String> format(Declaration declaration, FormatType input) {
        return Collections.singletonList(content);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JSPrimitiveType that = (JSPrimitiveType) o;
        return Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(content);
    }
}
