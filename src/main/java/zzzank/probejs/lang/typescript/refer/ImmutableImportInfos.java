package zzzank.probejs.lang.typescript.refer;

import zzzank.probejs.lang.java.clazz.ClassPath;

import java.util.Map;

/**
 * @author ZZZank
 */
public abstract class ImmutableImportInfos extends ImportInfos {
    ImmutableImportInfos(Map<ClassPath, ImportInfo> raw) {
        super(raw);
    }
}
