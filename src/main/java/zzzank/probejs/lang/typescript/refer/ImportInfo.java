package zzzank.probejs.lang.typescript.refer;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import zzzank.probejs.lang.java.clazz.ClassPath;

import java.util.EnumSet;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author ZZZank
 */
@AllArgsConstructor
@EqualsAndHashCode
public final class ImportInfo {

    public final ClassPath path;
    public final EnumSet<ImportType> types;

    public static ImportInfo of(ClassPath path, ImportType type, ImportType... rest) {
        return new ImportInfo(Objects.requireNonNull(path), EnumSet.of(type, rest));
    }

    public ImportInfo addType(@NotNull ImportType type) {
        types.add(type);
        return this;
    }

    /**
     * @param dedupedSymbol deduplicated symbol name, generated by {@link zzzank.probejs.lang.typescript.Declaration}
     * @return the string representing the actual import statement from this import info,
     * in 'import { ... } from ...' format
     */
    public @NotNull String toImport(@Nullable String dedupedSymbol) {
        val original = this.path.getName();
        val hasAltName = dedupedSymbol != null && !original.equals(dedupedSymbol);
        val names = this.types.stream()
            .map(type -> hasAltName
                ? type.fmt(original)
                : String.format("%s as %s", type.fmt(original), type.fmt(dedupedSymbol))
            )
            .collect(Collectors.joining(", "));

        // Underscores can be recognized by using a global export
        return String.format("import { %s } from \"packages/%s\"",
            names, this.path.getTypeScriptPath()
        );
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
