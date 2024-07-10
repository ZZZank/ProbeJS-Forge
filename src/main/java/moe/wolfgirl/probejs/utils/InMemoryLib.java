package moe.wolfgirl.probejs.utils;

import com.google.common.jimfs.Jimfs;
import lombok.Getter;
import lombok.val;
import me.shedaniel.architectury.platform.Platform;
import moe.wolfgirl.probejs.ProbeJS;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipFile;

/**
 * @author ZZZank
 */
public class InMemoryLib implements AutoCloseable {

    private final FileSystem system;
    @Getter
    private final List<Path> paths;

    public InMemoryLib() throws IOException {
        this.system = Jimfs.newFileSystem(ProbeJS.MOD_ID);
        this.paths = new ArrayList<>();

        try (val zip = new ZipFile(Platform.getMod(ProbeJS.MOD_ID).getFilePath().toFile())) {
            val entries = zip.stream()
                .filter(e -> !e.isDirectory())
                .filter(e -> e.getName().endsWith(".jar"))
                .filter(e -> e.getName().contains("/META-INF/jars"))
                .collect(Collectors.toList());

            for (val entry : entries) {
                val nameParts = entry.getName().split("/");
                val path = system.getPath(nameParts[nameParts.length - 1]);
                val out = new FileOutputStream(path.toFile());

                paths.add(path);
                FileUtils.transferTo(zip.getInputStream(entry), out);
            }
        }
    }

    @Override
    public void close() throws Exception {
        system.close();
    }

    public URL[] getURLs() throws MalformedURLException {
        val urls = new URL[this.paths.size()];
        for (int i = 0; i < this.paths.size(); i++) {
            urls[i] = this.paths.get(i).toUri().toURL();
        }
        return urls;
    }
}
