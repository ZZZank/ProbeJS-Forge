package zzzank.probejs.lang.typescript.refer;

import zzzank.probejs.utils.CollectUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Stream;

/**
 * @author ZZZank
 */
public final class SingletonImportInfos extends ImmutableImportInfos {
    SingletonImportInfos(ImportInfo info) {
        super(Collections.singletonMap(info.path, info));
    }

    private ImportInfo value() {
        return CollectUtils.anyIn(this.raw.values());
    }

    @Override
    public ImportInfos add(ImportInfo info) {
        return ImportInfos.of(value());
    }

    @Override
    public ImportInfos addAll(Collection<ImportInfo> infos) {
        return ImportInfos.of(infos);
    }

    @Override
    public ImportInfos addAll(Stream<ImportInfo> infos) {
        return ImportInfos.of(Stream.concat(infos, Stream.of(value())));
    }

    @Override
    public ImportInfos addAll(ImportInfos other) {
        return other instanceof ImmutableImportInfos
            ? ImportInfos.of(other).add(value())
            : other.add(value());
    }
}
