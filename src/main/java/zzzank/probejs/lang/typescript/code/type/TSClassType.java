package zzzank.probejs.lang.typescript.code.type;

import zzzank.probejs.lang.java.clazz.ClassPath;
import zzzank.probejs.lang.typescript.Declaration;
import zzzank.probejs.lang.typescript.refer.ImportInfo;
import zzzank.probejs.lang.typescript.refer.ImportInfos;

import java.util.Collections;
import java.util.List;

public class TSClassType extends BaseType {
    public ClassPath classPath;

    public TSClassType(ClassPath classPath) {
        this.classPath = classPath;
    }

    @Override
    public ImportInfos getImportInfos() {
        return ImportInfos.of(ImportInfo.of(classPath));
    }

    @Override
    public List<String> format(Declaration declaration, FormatType input) {
        return Collections.singletonList(declaration.getSymbol(classPath, input == FormatType.INPUT));
    }
}
