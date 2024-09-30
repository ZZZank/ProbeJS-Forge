package zzzank.probejs.lang.typescript.refer;

import lombok.AllArgsConstructor;
import zzzank.probejs.lang.java.clazz.ClassPath;

import java.util.EnumSet;

/**
 * @author ZZZank
 */
@AllArgsConstructor
public final class ImportInfo {

    public final ClassPath path;
    public final EnumSet<ImportType> types;

    public static ImportInfo of(ClassPath path, ImportType type, ImportType... rest) {
        return new ImportInfo(path, EnumSet.of(type, rest));
    }

    public ImportInfo withType(ImportType type) {
        types.add(type);
        return this;
    }

    public static ImportInfo ofDefault(ClassPath path) {
        return of(path, ImportType.TYPE, ImportType.ORIGINAL);
    }

    public static ImportInfo ofType(ClassPath path) {
        return of(path, ImportType.TYPE, ImportType.ORIGINAL);
    }

    public static ImportInfo ofOriginal(ClassPath path) {
        return of(path, ImportType.ORIGINAL, ImportType.TYPE);
    }

    public static ImportInfo ofStatic(ClassPath path) {
        return of(path, ImportType.STATIC);
    }
}
