package zzzank.probejs.lang.typescript.code.type.ts;

import lombok.val;
import zzzank.probejs.lang.java.clazz.ClassPath;
import zzzank.probejs.lang.typescript.Declaration;
import zzzank.probejs.lang.typescript.code.type.BaseType;
import zzzank.probejs.lang.typescript.refer.ImportInfo;
import zzzank.probejs.lang.typescript.refer.ImportInfos;
import zzzank.probejs.lang.typescript.refer.ImportType;

import javax.annotation.Nonnull;

public class TSClassType extends BaseType {
    public ClassPath classPath;

    public TSClassType(ClassPath classPath) {
        this.classPath = classPath;
    }

    @Override
    public ImportInfos getImportInfos(@Nonnull FormatType type) {
        return ImportInfos.of(switch (type) {
            case RETURN -> ImportInfo.ofOriginal(classPath);
            case INPUT -> ImportInfo.ofType(classPath);
            default -> ImportInfo.of(classPath);
        });
    }

    @Override
    public String line(Declaration declaration, FormatType formatType) {
        val symbol = declaration.getSymbol(classPath);
        if (formatType == FormatType.INPUT) {
            return ImportType.TYPE.fmt(symbol);
        }
        return symbol;
    }
}
