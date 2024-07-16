package moe.wolfgirl.probejs.utils;

import com.google.gson.JsonObject;
import dev.latvian.kubejs.util.UtilsJS;
import lombok.val;
import moe.wolfgirl.probejs.ProbeJS;
import moe.wolfgirl.probejs.ProbePaths;
import net.minecraftforge.fml.ModList;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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
        val pjs = ModList.get().getModFileById(ProbeJS.MOD_ID).getFile().getFilePath().toFile();

        try (val zip = new ZipFile(pjs)) {

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
        ProbeJS.LOGGER.debug(urls);
        return urls;
    }

    public static void clear() {
        if (urls == null) {
            throw new IllegalStateException("already cleared");
        }
        PATH.toFile().delete();
        urls = null;
    }
}
