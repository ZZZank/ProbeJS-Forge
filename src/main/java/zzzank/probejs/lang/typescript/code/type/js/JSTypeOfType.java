package zzzank.probejs.lang.typescript.code.type.js;

import zzzank.probejs.lang.typescript.Declaration;
import zzzank.probejs.lang.typescript.code.type.BaseType;
import zzzank.probejs.lang.typescript.code.type.TSClassType;
import zzzank.probejs.lang.typescript.code.type.TSStaticType;
import zzzank.probejs.lang.typescript.refer.ImportInfos;
import zzzank.probejs.lang.typescript.refer.ImportType;

import java.util.Collections;
import java.util.List;

public class JSTypeOfType extends BaseType {

    public final BaseType inner;

    public JSTypeOfType(BaseType inner) {
        //            this.isInterface = clazz.attribute.isInterface;
        //            isInterface = false;
        this.inner = inner instanceof TSClassType cType
            ? new TSStaticType(cType.classPath)
            : inner;
    }

    @Override
    public ImportInfos getImportInfos() {
        return inner.getImportInfos();
    }

    @Override
    public List<String> format(Declaration declaration, FormatType input) {
        return Collections.singletonList(String.format("typeof %s", inner.line(declaration, FormatType.RETURN)));
    }
}
