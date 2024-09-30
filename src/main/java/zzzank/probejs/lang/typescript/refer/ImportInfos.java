package zzzank.probejs.lang.typescript.refer;

import lombok.val;
import org.jetbrains.annotations.NotNull;
import zzzank.probejs.lang.java.clazz.ClassPath;
import zzzank.probejs.lang.typescript.code.Code;

import java.util.*;
import java.util.stream.Stream;

/**
 * @author ZZZank
 */
public class ImportInfos implements Iterable<ImportInfo> {

    private final Map<ClassPath, ImportInfo> raw;

    ImportInfos() {
        this.raw = new HashMap<>();
    }

    public static ImmutableImportInfos ofImmutableEmpty() {
        return ImmutableImportInfos.EMPTY;
    }

    public static ImmutableImportInfos ofImmutable(Collection<ImportInfo> infos) {
        return new ImmutableImportInfos(infos);
    }

    public static ImportInfos of(ImportInfos other) {
        return new ImportInfos().addAll(other);
    }

    public static ImportInfos of(ImportInfo... initial) {
        val infos = new ImportInfos();
        for (val info : initial) {
            infos.add(info);
        }
        return infos;
    }

    public static ImportInfos of(Collection<ImportInfo> infos) {
        return new ImportInfos().addAll(infos);
    }

    public static ImportInfos of(Stream<ImportInfo> infos) {
        return new ImportInfos().addAll(infos);
    }

    public ImportInfos add(ImportInfo info) {
        addImpl(info);
        return this;
    }

    protected void addImpl(ImportInfo info) {
        val old = raw.put(info.path, info);
        if (old != null) {
            info.types.addAll(old.types);
        }
    }

    public ImportInfos addAll(ImportInfos other) {
        return addAll(other.getImports());
    }

    public ImportInfos addAll(Stream<ImportInfo> infos) {
        infos.forEach(this::add);
        return this;
    }

    public ImportInfos addAll(Collection<ImportInfo> infos) {
        for (val info : infos) {
            add(info);
        }
        return this;
    }

    public ImportInfos fromCode(Code code) {
        return addAll(code != null ? code.getImportInfos().getImports() : Collections.emptySet());
    }

    public ImportInfos fromCodes(Stream<? extends Code> codes) {
        codes.forEach(this::fromCode);
        return this;
    }

    public ImportInfos fromCodes(Collection<? extends Code> codes) {
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
