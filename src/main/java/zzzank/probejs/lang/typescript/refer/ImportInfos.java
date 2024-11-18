package zzzank.probejs.lang.typescript.refer;

import lombok.val;
import org.jetbrains.annotations.NotNull;
import zzzank.probejs.lang.java.clazz.ClassPath;
import zzzank.probejs.lang.typescript.code.Code;
import zzzank.probejs.utils.Mutable;

import java.util.*;
import java.util.stream.Stream;

/**
 * @author ZZZank
 */
public class ImportInfos implements Iterable<ImportInfo> {

    protected final Map<ClassPath, ImportInfo> raw;

    protected ImportInfos(Map<ClassPath, ImportInfo> raw) {
        this.raw = raw;
    }

    public static ImportInfos of(@NotNull ImportInfos toCopy) {
        return toCopy instanceof ImmutableImportInfos
            ? toCopy
            : new ImportInfos(new HashMap<>(toCopy.raw));
    }

    public static ImmutableImportInfos of() {
        return EmptyImportInfos.INSTANCE;
    }

    public static ImmutableImportInfos of(@NotNull ImportInfo info) {
        return new SingletonImportInfos(info);
    }

    public static ImportInfos of(@NotNull ImportInfo... initial) {
        return new ImportInfos(new HashMap<>()).addAll(Arrays.asList(initial));
    }

    public static ImportInfos of(@NotNull Collection<ImportInfo> infos) {
        return new ImportInfos(new HashMap<>()).addAll(infos);
    }

    public static ImportInfos of(@NotNull Stream<ImportInfo> infos) {
        return new ImportInfos(new HashMap<>()).addAll(infos);
    }

    public ImportInfos add(@NotNull ImportInfo info) {
        val old = raw.put(info.path, info);
        if (old != null) {
            info.mergeWith(old);
        }
        return this;
    }

    public ImportInfos addAll(@NotNull ImportInfos other) {
        return addAll(other.getImports());
    }

    public ImportInfos addAll(@NotNull Stream<ImportInfo> infos) {
        val hold = Mutable.of(this);
        infos.forEach(info -> hold.set(hold.get().add(info)));
        return this;
    }

    public ImportInfos addAll(@NotNull Collection<ImportInfo> infos) {
        ImportInfos self = this;
        for (val info : infos) {
            self = add(info);
        }
        return self;
    }

    public ImportInfos fromCode(Code code) {
        return addAll(code != null ? code.getImportInfos() : ImportInfos.of());
    }

    public ImportInfos fromCodes(@NotNull Stream<? extends Code> codes) {
        codes.forEach(this::fromCode);
        return this;
    }

    public ImportInfos fromCodes(@NotNull Collection<? extends Code> codes) {
        for (val code : codes) {
            fromCode(code);
        }
        return this;
    }

    public Collection<ImportInfo> getImports() {
        return raw.values();
    }

    public Map<ClassPath, ImportInfo> getRaw() {
        return Collections.unmodifiableMap(raw);
    }

    @Override
    public @NotNull Iterator<ImportInfo> iterator() {
        return raw.values().iterator();
    }
}
