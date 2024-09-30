package zzzank.probejs.lang.typescript.refer;

import lombok.val;

import java.util.Collection;
import java.util.Collections;

/**
 * @author ZZZank
 */
public class ImmutableImportInfos extends ImportInfos {
    public static final ImmutableImportInfos EMPTY = ImportInfos.ofImmutable(Collections.emptySet());

    public ImmutableImportInfos(Collection<? extends ImportInfo> infos) {
        super();
        for (val info : infos) {
            addImpl(info);
        }
    }

    @Deprecated
    @Override
    public ImportInfos add(ImportInfo info) {
        throw new IllegalStateException("Not allowed");
    }
}
