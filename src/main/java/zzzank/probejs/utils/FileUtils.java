package zzzank.probejs.utils;

import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import dev.latvian.kubejs.KubeJSPaths;
import zzzank.probejs.ProbeJS;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Consumer;

public class FileUtils {
    public static void forEachFile(Path basePath, Consumer<Path> callback) throws IOException {
        try (var dirStream = Files.newDirectoryStream(basePath)) {
            for (Path path : dirStream) {
                if (Files.isDirectory(path)) {
                    forEachFile(path, callback);
                } else {
                    callback.accept(path);
                }
            }
        }
    }

    public static void writeMergedConfig(Path path, String config) throws IOException {
        JsonObject updates = ProbeJS.GSON.fromJson(config, JsonObject.class);
        JsonObject read = Files.exists(path)
            ? ProbeJS.GSON.fromJson(Files.newBufferedReader(path), JsonObject.class)
            : new JsonObject();
        if (read == null) {
            read = new JsonObject();
        }
        JsonObject original = (JsonObject) JsonUtils.mergeJsonRecursively(read, updates);
        JsonWriter jsonWriter = ProbeJS.GSON_WRITER.newJsonWriter(Files.newBufferedWriter(path));
        jsonWriter.setIndent("    ");
        ProbeJS.GSON_WRITER.toJson(original, JsonObject.class, jsonWriter);
        jsonWriter.close();
    }

    @Nullable
    public static Path parseSourcePath(String name) {
        if (!name.contains(":")) return null;
        String[] parts = name.split(":", 2);
        Path base;
        if (parts[0].equals("client_scripts")) {
            base = KubeJSPaths.CLIENT_SCRIPTS;
        } else if (parts[0].equals("server_scripts")) {
            base = KubeJSPaths.SERVER_SCRIPTS;
        } else if (parts[0].equals("startup_scripts")) {
            base = KubeJSPaths.STARTUP_SCRIPTS;
        } else {
            return null;
        }
        return base.resolve(parts[1]);
    }

    public static long transferTo(InputStream in, OutputStream out) throws IOException {
        Objects.requireNonNull(in, "in");
        Objects.requireNonNull(out, "out");
        long transferred = 0;
        byte[] buffer = new byte[16384];
        int read;
        while ((read = in.read(buffer, 0, 16384)) >= 0) {
            out.write(buffer, 0, read);
            if (transferred < Long.MAX_VALUE) {
                try {
                    transferred = Math.addExact(transferred, read);
                } catch (ArithmeticException ignore) {
                    transferred = Long.MAX_VALUE;
                }
            }
        }
        return transferred;
    }
}
