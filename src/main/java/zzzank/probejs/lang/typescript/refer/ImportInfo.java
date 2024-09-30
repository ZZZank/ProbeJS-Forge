package zzzank.probejs.lang.typescript.refer;

import lombok.AllArgsConstructor;
import zzzank.probejs.lang.java.clazz.ClassPath;

import java.util.EnumSet;

/**
 * @author ZZZank
 */
@AllArgsConstructor
public final class ImportInfo {
    public static final String INPUT_TEMPLATE = "%s$$Type";
    public static final String STATIC_TEMPLATE = "%s$$Static";

    public final ClassPath path;
    public final EnumSet<ImportType> types;

    public static ImportInfo of(ClassPath path, ImportType type, ImportType... rest) {
        return new ImportInfo(path, EnumSet.of(type, rest));
    }

    public static ImportInfo ofType(ClassPath path) {
        return of(path, ImportType.TYPE);
    }

    public static ImportInfo ofOriginal(ClassPath path) {
        return of(path, ImportType.ORIGINAL);
    }

    public static ImportInfo ofStatic(ClassPath path) {
        return of(path, ImportType.STATIC);
    }
}
