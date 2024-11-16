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

    protected final Map<ClassPath, ImportInfo> raw;

    protected ImportInfos(Map<ClassPath, ImportInfo> raw) {
        this.raw = raw;
    }

    public static ImportInfos of(ImportInfos toCopy) {
        return new ImportInfos(new HashMap<>(toCopy.raw));
    }

    public static ImportInfos of() {
        return EmptyImportInfos.INSTANCE;
    }

    public static ImportInfos of(ImportInfo info) {
        return new SingletonImportInfos(info);
    }

    public static ImportInfos of(ImportInfo... initial) {
        return new ImportInfos(new HashMap<>()).addAll(Arrays.asList(initial));
    }

    public static ImportInfos of(Collection<ImportInfo> infos) {
        return new ImportInfos(new HashMap<>()).addAll(infos);
    }

    public static ImportInfos of(Stream<ImportInfo> infos) {
        return new ImportInfos(new HashMap<>()).addAll(infos);
    }

    public ImportInfos add(ImportInfo info) {
        val old = raw.put(info.path, info);
        if (old != null) {
            info.mergeWith(old);
        }
        return this;
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
