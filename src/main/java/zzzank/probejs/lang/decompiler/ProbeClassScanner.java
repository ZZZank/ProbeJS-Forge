package zzzank.probejs.lang.decompiler;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.val;
import zzzank.probejs.ProbeJS;
import zzzank.probejs.utils.ReflectUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Getter
public class ProbeClassScanner {

    private final static String CLASS_SUFFIX = ".class";
    private final Set<Class<?>> scannedClasses = new HashSet<>();

    public void acceptFile(File file) throws IOException {
        try (val jarFile = new ZipFile(file)) {
            val modClassesScanner = new ModClassesScanner(jarFile);
            scannedClasses.addAll(modClassesScanner.scanClasses());
            if (!modClassesScanner.mixinPackages.isEmpty()) {
                ProbeJS.LOGGER.debug(
                    "mod '{}' provides {} mixin packages, {} classes are filtered out",
                    file.getName(),
                    modClassesScanner.mixinPackages.size(),
                    modClassesScanner.mixinFiltered
                );
            }
        }
        ProbeJS.LOGGER.debug("scanned file '{}', current class count: {}", file.getName(), scannedClasses.size());
    }

    /**
     * get all loaded classes by reading {@link ClassLoader#classes} from {@code Thread.currentThread().getContextClassLoader()}
     * and its parents
     */
    private void fromClassLoader() throws IOException {
        val loader = Thread.currentThread().getContextClassLoader();
        val resources = loader.getResources("");
        while (resources.hasMoreElements()) {
            val res = resources.nextElement();
            if (res.getProtocol().equals("file")) {
                scannedClasses.addAll(loadClassByPath(null, res.getPath(), loader));
            }
        }
        ProbeJS.LOGGER.debug("scanned: {}", scannedClasses.size());
    }

    private List<Class<?>> loadClassByPath(String root, String path, ClassLoader load) {
        File f = new File(path);
        if (root == null) {
            root = f.getPath();
        }

        if (!f.isFile() || !f.getName().endsWith(CLASS_SUFFIX)) {
            File[] fs = f.listFiles();
            if (fs == null) {
                return Collections.emptyList();
            }
            val classes = new ArrayList<Class<?>>();
            for (File file : fs) {
                classes.addAll(loadClassByPath(root, file.getPath(), load));
            }
            return classes;
        }

        try {
            val classPath = f.getPath();
            val className = classPath
                .substring(root.length() + 1, classPath.length() - CLASS_SUFFIX.length())
                .replace('/', '.')
                .replace('\\', '.');
            return Collections.singletonList(load.loadClass(className));
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }

    static class ModClassesScanner {

        private final ZipFile file;
        private final HashSet<String> mixinPackages;
        private int mixinFiltered;

        ModClassesScanner(ZipFile modJar) {
            this.file = modJar;
            this.mixinPackages = new HashSet<>();
            mixinFiltered = 0;
        }

        void fetchMixinPackages(ZipEntry entry) {
            if (!entry.getName().endsWith(".mixins.json")) {
                return;
            }
            try {
                val inputStream = file.getInputStream(entry);
                val jObj = ProbeJS.GSON.fromJson(new InputStreamReader(inputStream), JsonObject.class);
                val packageName = jObj.get("package").getAsString();
                mixinPackages.add(packageName);
            } catch (IOException ignored) {
            }
        }

        boolean notFromMixinPackages(String className) {
            for (String mixinPackage : mixinPackages) {
                if (className.startsWith(mixinPackage)) {
                    mixinFiltered += 1;
                    return false;
                }
            }
            return true;
        }

        Set<Class<?>> scanClasses() {
            return file.stream()
                .filter(e -> !e.isDirectory())
                .peek(this::fetchMixinPackages)
                .map(ZipEntry::getName)
                .filter(name -> name.endsWith(CLASS_SUFFIX))
                .map(name -> name.substring(0, name.length() - CLASS_SUFFIX.length()).replace("/", "."))
                .filter(this::notFromMixinPackages)
                .map(ReflectUtils::classOrNull)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        }
    }
}
