package zzzank.probejs.lang.typescript.refer;

import com.google.common.collect.ImmutableMap;

import java.util.Collection;
import java.util.stream.Stream;

/**
 * @author ZZZank
 */
public final class EmptyImportInfos extends ImmutableImportInfos {
    public static final EmptyImportInfos INSTANCE = new EmptyImportInfos();

    private EmptyImportInfos() {
        super(ImmutableMap.of());
    }

    @Override
    public ImportInfos add(ImportInfo other) {
        return ImportInfos.of(other);
    }

    @Override
    public ImportInfos addAll(Stream<ImportInfo> infos) {
        return ImportInfos.of(infos);
    }

    @Override
    public ImportInfos addAll(Collection<ImportInfo> infos) {
        return ImportInfos.of(infos);
    }

    @Override
    public ImportInfos addAll(ImportInfos other) {
        return other;
    }
}
