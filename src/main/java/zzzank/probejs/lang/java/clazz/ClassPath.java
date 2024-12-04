package zzzank.probejs.lang.java.clazz;

import dev.latvian.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.util.HideFromJS;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import zzzank.probejs.features.rhizo.RemapperBridge;
import zzzank.probejs.lang.java.ClassRegistry;
import zzzank.probejs.utils.CollectUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Pattern;

@AllArgsConstructor
@ToString
public final class ClassPath implements Comparable<ClassPath> {

    public static final String TS_PATH_PREFIX = "packages/";
    public static final Pattern SPLIT = Pattern.compile("\\.");

    public final String[] parts;

    public static @NotNull ClassPath fromRaw(String className) {
        if (className == null || className.isEmpty()) {
            throw new IllegalArgumentException("'className' is " + (className == null ? "null" : "empty"));
        }
        return new ClassPath(SPLIT.split(className));
    }

    public static ClassPath fromJava(@NotNull Class<?> clazz) {
        val name = RemapperBridge.remapClass(Objects.requireNonNull(clazz));
        val parts = SPLIT.split(name);
        parts[parts.length - 1] = "$" + parts[parts.length - 1];
        return new ClassPath(parts);
    }

    public static ClassPath fromTS(@NotNull String typeScriptPath) {
        if (!typeScriptPath.startsWith(TS_PATH_PREFIX)) {
            throw new IllegalArgumentException(String.format("path '%s' is not ProbeJS TS path", typeScriptPath));
        }
        val names = typeScriptPath.substring(TS_PATH_PREFIX.length()).split("/");
        return new ClassPath(names);
    }

    public String getName() {
        return parts[parts.length - 1];
    }

    public String getConcatenated(String sep) {
        return String.join(sep, parts);
    }

    public String getDirectPath() {
        return getConcatenated(".");
    }

    public String getJavaPath() {
        val copy = CollectUtils.ofList(parts);
        val last = parts[parts.length - 1];
        if (last.startsWith("$")) {
            copy.set(parts.length - 1, last.substring(1));
        }
        return String.join(".", copy);
    }

    public String getTSPath() {
        return TS_PATH_PREFIX + getConcatenated("/");
    }

    @Nullable
    @HideFromJS
    public Clazz toClazz() {
        return ClassRegistry.REGISTRY.foundClasses.get(this);
    }

    public String[] getPackage() {
        return Arrays.copyOf(this.parts, parts.length - 1);
    }

    public String getConcatenatedPackage(String sep) {
        return String.join(sep, getPackage());
    }

    public Path getDirPath(Path base) {
        return base.resolve(getConcatenatedPackage("/"));
    }

    public Path makePath(Path base) {
        Path full = getDirPath(base);
        if (Files.notExists(full)) {
            UtilsJS.tryIO(() -> Files.createDirectories(full));
        }
        return full;
    }

    @Override
    public int compareTo(@NotNull ClassPath another) {
        val sizeThis = parts.length;
        val sizeOther = another.parts.length;
        val sizeCompare = Integer.min(sizeOther, sizeThis);

        val common = getCommonPartsCount(another);
        if (common == sizeCompare) { //
            return Integer.compare(sizeThis, sizeOther);
        }
        return parts[common].compareTo(another.parts[common]);
    }

    public int getCommonPartsCount(@NotNull ClassPath another) {
        val sizeThis = parts.length;
        val sizeOther = another.parts.length;
        val sizeCompare = Integer.min(sizeOther, sizeThis);
        for (int i = 0; i < sizeCompare; i++) {
            if (!parts[i].equals(another.parts[i])) {
                return i;
            }
        }
        return sizeCompare;
    }

    public boolean equals(final Object o) {
        return o instanceof ClassPath other && Arrays.equals(this.parts, other.parts);
    }

    public int hashCode() {
        return Arrays.hashCode(this.parts);
    }
}
