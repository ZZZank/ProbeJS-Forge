package zzzank.probejs.lang.typescript.refer;

import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Stream;

/**
 * @author ZZZank
 */
final class SingletonImportInfos extends ImmutableImportInfos {
    SingletonImportInfos(ImportInfo info) {
        super(Collections.singletonMap(info.path, info));
    }

    private ImportInfo value() {
        return this.raw.values().iterator().next();
    }

    @Override
    public ImportInfos add(@NotNull ImportInfo info) {
        val value = this.value();
        if (value.path.equals(info.path)) {
            value.mergeWith(info);
            return this;
        }
        return ImportInfos.of(value, info);
    }

    @Override
    public ImportInfos addAll(@NotNull Collection<ImportInfo> infos) {
        return ImportInfos.of(infos).add(value());
    }

    @Override
    public ImportInfos addAll(@NotNull Stream<ImportInfo> infos) {
        return ImportInfos.of(infos).add(value());
    }

    @Override
    public ImportInfos addAll(@NotNull ImportInfos other) {
        return ImportInfos.of(other).add(value());
    }
}
