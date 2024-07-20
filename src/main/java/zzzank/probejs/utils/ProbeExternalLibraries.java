package zzzank.probejs.utils;

import com.google.gson.JsonObject;
import dev.latvian.kubejs.util.UtilsJS;
import lombok.val;
import zzzank.probejs.ProbeJS;
import zzzank.probejs.ProbePaths;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.zip.ZipFile;

/**
 * @author ZZZank
 */
public abstract class ProbeExternalLibraries {

    public static final Path PATH = ProbePaths.PROBE.resolve("lib");
    private static URL[] urls = null;

    public static void setup() {
        if (Files.notExists(PATH)) {
            UtilsJS.tryIO(() -> Files.createDirectories(PATH));
        }

        if (urls != null) {
            throw new IllegalStateException("already setup, please clear first");
        }

        val names = new ArrayList<String>();

        try (val zip = new ZipFile(locateModFile())) {

            val entry = zip.getEntry("META-INF/jarjar/metadata.json");
            val in = zip.getInputStream(entry);
            val jObj = ProbeJS.GSON.fromJson(new InputStreamReader(in), JsonObject.class);

            for (val elem : jObj.get("jars").getAsJsonArray()) {
                val asObj = elem.getAsJsonObject();

                val name = asObj.get("identifier").getAsJsonObject().get("artifact").getAsString() + ".jar";
                names.add(name);
                val path = asObj.get("path").getAsString();

                FileUtils.transferTo(
                    zip.getInputStream(zip.getEntry(path)),
                    new FileOutputStream(PATH.resolve(name).toFile())
                );
            }

            urls = new URL[names.size()];
            for (int i = 0; i < names.size(); i++) {
                urls[i] = PATH.resolve(names.get(i)).toUri().toURL();
            }
        } catch (IOException e) {
            throw new RuntimeException("Cannot read dependencies info from ProbeJS jar", e);
        }
    }

    public static URL[] get() {
//        ProbeJS.LOGGER.debug(urls);
        return urls;
    }

    public static void clear() {
        if (urls == null) {
            throw new IllegalStateException("already cleared");
        }
        PATH.toFile().delete();
        urls = null;
    }

    private static File locateModFile() throws MalformedURLException {
        val mods = ProbePaths.GAMEDIR.resolve("mods");
        if (!Files.exists(mods)) {
            return null;
        }
        for (File mod : mods.toFile().listFiles()) {
            if (mod.getName().startsWith("probejs")) {
                return mod;
            }
        }
        return null;
    }

    public static URL resolveURL(String name) {
        try {
            return PATH.resolve(name).toUri().toURL();
        } catch (MalformedURLException e) {
            return null;
        }
    }
}
