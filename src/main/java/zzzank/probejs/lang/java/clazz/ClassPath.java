package zzzank.probejs.lang.java.clazz;

import com.github.bsideup.jabel.Desugar;
import dev.latvian.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.util.HideFromJS;
import lombok.val;
import zzzank.probejs.features.rhizo.RemapperBridge;
import zzzank.probejs.lang.java.ClassRegistry;

import java.lang.reflect.TypeVariable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Desugar
public record ClassPath(List<String> parts) {

    private static final Pattern SPLIT = Pattern.compile("\\.");

    private static List<String> transformJavaClass(Class<?> clazz) {
        val name = RemapperBridge.remapClass(clazz);
        val parts = SPLIT.split(name);
        parts[parts.length - 1] = "$" + parts[parts.length - 1];
        return Arrays.asList(parts);
    }

    public ClassPath(String className) {
        this(Arrays.asList(SPLIT.split(className)));
    }

    public ClassPath(Class<?> clazz) {
        this(transformJavaClass(clazz));
    }

    public String getName() {
        return parts.get(parts.size() - 1);
    }

    public String getConcatenated(String sep) {
        return String.join(sep, parts);
    }

    public String getClassPath() {
        return getConcatenated(".");
    }

    public String getClassPathJava() {
        List<String> copy = new ArrayList<>(parts);
        String last = copy.get(copy.size() - 1);
        if (last.startsWith("$")) {
            last = last.substring(1);
        }
        copy.set(copy.size() - 1, last);
        return String.join(".", copy);
    }

    public String getTypeScriptPath() {
        return getConcatenated("/");
    }

    @HideFromJS
    public Class<?> forName() throws ClassNotFoundException {
        return Class.forName(getClassPathJava());
    }

    public List<String> getGenerics() throws ClassNotFoundException {
        TypeVariable<?>[] variables = forName().getTypeParameters();
        return Arrays.stream(variables).map(TypeVariable::getName).collect(Collectors.toList());
    }

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
}
