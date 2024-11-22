package zzzank.probejs.lang.typescript.code.type.utility;

import lombok.val;
import zzzank.probejs.lang.java.clazz.ClassPath;
import zzzank.probejs.lang.typescript.Declaration;
import zzzank.probejs.lang.typescript.code.type.ts.TSClassType;
import zzzank.probejs.lang.typescript.refer.ImportInfo;
import zzzank.probejs.lang.typescript.refer.ImportInfos;
import zzzank.probejs.lang.typescript.refer.ImportType;

import java.util.Collections;
import java.util.List;

/**
 * @author ZZZank
 */
public class StaticType extends TSClassType {
    public StaticType(ClassPath classPath) {
        super(classPath);
    }

    @Override
    public ImportInfos getImportInfos(FormatType type) {
        return ImportInfos.of(ImportInfo.ofStatic(classPath));
    }

    @Override
    public List<String> format(Declaration declaration, FormatType input) {
        val name = declaration.getSymbol(classPath);
        return Collections.singletonList(ImportType.STATIC.fmt(name));
    }
}
