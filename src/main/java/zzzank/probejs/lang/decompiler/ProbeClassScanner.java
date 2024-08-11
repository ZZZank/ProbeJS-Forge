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
                .map(c -> ReflectUtils.classOrNull(c, false, false))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        }
    }
}
