package zzzank.probejs.lang.java.clazz;

import com.github.bsideup.jabel.Desugar;
import dev.latvian.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.util.HideFromJS;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import zzzank.probejs.features.rhizo.RemapperBridge;
import zzzank.probejs.lang.java.ClassRegistry;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

@Desugar
public record ClassPath(List<String> parts) implements Comparable<ClassPath> {

    public static final String TS_PATH_PREFIX = "packages/";

    private static final Pattern SPLIT = Pattern.compile("\\.");

    public static ClassPath fromJava(@NotNull String className) {
        return new ClassPath(Arrays.asList(SPLIT.split(className)));
    }

    public static ClassPath fromJava(@NotNull Class<?> clazz) {
        val name = RemapperBridge.remapClass(Objects.requireNonNull(clazz));
        val parts = SPLIT.split(name);
        parts[parts.length - 1] = "$" + parts[parts.length - 1];
        return new ClassPath(Arrays.asList(parts));
    }

    public static ClassPath fromTS(@NotNull String typeScriptPath) {
        if (!typeScriptPath.startsWith(TS_PATH_PREFIX)) {
            throw new IllegalArgumentException(String.format("path '%s' is not ProbeJS TS path", typeScriptPath));
        }
        val names = typeScriptPath.substring(TS_PATH_PREFIX.length()).split("/");
        return new ClassPath(Arrays.asList(names));
    }

    public String getName() {
        return parts.get(parts.size() - 1);
    }

    public String getConcatenated(String sep) {
        return String.join(sep, parts);
    }

    public String getDirectPath() {
        return getConcatenated(".");
    }

    public String getJavaPath() {
        val copy = new ArrayList<>(parts);
        val last = copy.get(copy.size() - 1);
        if (last.startsWith("$")) {
            copy.set(copy.size() - 1, last.substring(1));
        }
        return String.join(".", copy);
    }

    public String getTSPath() {
        return TS_PATH_PREFIX + getConcatenated("/");
    }

    @HideFromJS
    public Class<?> forName() throws ClassNotFoundException {
        return Class.forName(getJavaPath());
    }

    @Nullable
    @HideFromJS
    public Clazz toClazz() {
        return ClassRegistry.REGISTRY.foundClasses.get(this);
    }

    public List<String> getPackage() {
        List<String> classPath = new ArrayList<>(parts);
        classPath.remove(classPath.size() - 1);
        return classPath;
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
        val sizeThis = parts.size();
        val sizeOther = another.parts.size();
        val sizeCompare = Integer.min(sizeOther, sizeThis);

        val common = getCommonPartsCount(another);
        if (common == sizeCompare) { //
            return Integer.compare(sizeThis, sizeOther);
        }
        return parts.get(common).compareTo(another.parts.get(common));
    }

    public int getCommonPartsCount(@NotNull ClassPath another) {
        val sizeThis = parts.size();
        val sizeOther = another.parts.size();
        val sizeCompare = Integer.min(sizeOther, sizeThis);
        for (int i = 0; i < sizeCompare; i++) {
            if (!parts.get(i).equals(another.parts.get(i))) {
                return i;
            }
        }
        return sizeCompare;
    }
}
