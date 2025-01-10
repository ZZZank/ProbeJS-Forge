package zzzank.probejs.lang.java.clazz;

import dev.latvian.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.util.HideFromJS;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import zzzank.probejs.lang.java.remap.RemapperBridge;
import zzzank.probejs.lang.java.ClassRegistry;
import zzzank.probejs.utils.CollectUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@ToString
public final class ClassPath implements Comparable<ClassPath> {

    public static final String TS_PATH_PREFIX = "packages/";
    public static final Pattern SPLIT = Pattern.compile("\\.");

    private final String[] parts;

    public static @NotNull ClassPath fromRaw(final String className) {
        if (className == null || className.isEmpty()) {
            throw new IllegalArgumentException("'className' is " + (className == null ? "null" : "empty"));
        }
        return new ClassPath(SPLIT.split(className));
    }

    public static ClassPath fromJava(final @NotNull Class<?> clazz) {
        val name = RemapperBridge.remapClass(Objects.requireNonNull(clazz));
        val parts = SPLIT.split(name);
        parts[parts.length - 1] = "$" + parts[parts.length - 1];
        return new ClassPath(parts);
    }

    public static ClassPath fromTS(final @NotNull String typeScriptPath) {
        if (!typeScriptPath.startsWith(TS_PATH_PREFIX)) {
            throw new IllegalArgumentException(String.format("path '%s' is not ProbeJS TS path", typeScriptPath));
        }
        val names = typeScriptPath.substring(TS_PATH_PREFIX.length()).split("/");
        return new ClassPath(names);
    }

    public String getPart(final int index) {
        return parts[index];
    }

    public List<String> getParts() {
        return Collections.unmodifiableList(Arrays.asList(this.parts));
    }

    public String getName() {
        return parts[parts.length - 1];
    }

    public String getJavaName() {
        val name = getName();
        return name.startsWith("$") ? name.substring(1) : name;
    }

    public String getConcatenated(final String sep) {
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
    public Clazz toClazz(ClassRegistry registry) {
        return registry.foundClasses.get(this);
    }

    public List<String> getPackage() {
        return getParts().subList(0, this.parts.length - 1);
    }

    public String getConcatenatedPackage(final String sep) {
        return String.join(sep, getPackage());
    }

    public Path getDirPath(final Path base) {
        return base.resolve(getConcatenatedPackage("/"));
    }

    public Path makePath(final Path base) {
        Path full = getDirPath(base);
        if (Files.notExists(full)) {
            UtilsJS.tryIO(() -> Files.createDirectories(full));
        }
        return full;
    }

    @Override
    public int compareTo(final @NotNull ClassPath another) {
        val sizeThis = parts.length;
        val sizeOther = another.parts.length;
        val sizeCompare = Integer.min(sizeOther, sizeThis);

        val common = getCommonPartsCount(another);
        if (common == sizeCompare) { //
            return Integer.compare(sizeThis, sizeOther);
        }
        return parts[common].compareTo(another.parts[common]);
    }

    public int getCommonPartsCount(final @NotNull ClassPath another) {
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
