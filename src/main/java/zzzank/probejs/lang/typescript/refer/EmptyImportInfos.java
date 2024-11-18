package zzzank.probejs.lang.typescript.refer;

import com.google.common.collect.ImmutableMap;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.stream.Stream;

/**
 * @author ZZZank
 */
final class EmptyImportInfos extends ImmutableImportInfos {
    public static final EmptyImportInfos INSTANCE = new EmptyImportInfos();

    private EmptyImportInfos() {
        super(ImmutableMap.of());
    }

    @Override
    public ImportInfos add(@NotNull ImportInfo other) {
        return ImportInfos.of(other);
    }

    @Override
    public ImportInfos addAll(@NotNull Stream<ImportInfo> infos) {
        return ImportInfos.of(infos);
    }

    @Override
    public ImportInfos addAll(@NotNull Collection<ImportInfo> infos) {
        return ImportInfos.of(infos);
    }

    @Override
    public ImportInfos addAll(@NotNull ImportInfos other) {
        return ImportInfos.of(other);
    }
}
