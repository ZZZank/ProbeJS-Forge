package zzzank.probejs.lang.typescript.code.type.js;

import zzzank.probejs.lang.java.clazz.Clazz;
import zzzank.probejs.lang.typescript.Declaration;
import zzzank.probejs.lang.typescript.code.type.BaseType;
import zzzank.probejs.lang.typescript.code.type.TSClassType;
import zzzank.probejs.lang.typescript.refer.ImportInfos;

import java.util.Collections;
import java.util.List;

public class JSTypeOfType extends BaseType {

    public final BaseType inner;
    private final boolean isInterface;

    public JSTypeOfType(BaseType inner) {
        this.inner = inner;
        Clazz clazz = inner instanceof TSClassType classType ? classType.classPath.toClazz() : null;
        this.isInterface = clazz != null && clazz.attribute.isInterface;
    }

    @Override
    public ImportInfos getImportInfos() {
        return inner.getImportInfos();
    }

    @Override
    public List<String> format(Declaration declaration, FormatType input) {
        return Collections.singletonList(isInterface ?
            inner.line(declaration, FormatType.RETURN) :
            String.format("typeof %s", inner.line(declaration, FormatType.RETURN))
        );
    }
}
