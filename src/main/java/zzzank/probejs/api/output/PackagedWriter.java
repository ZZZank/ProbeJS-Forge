package zzzank.probejs.api.output;

import lombok.val;
import org.jetbrains.annotations.NotNull;
import zzzank.probejs.ProbeJS;
import zzzank.probejs.lang.typescript.TypeScriptFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * @author ZZZank
 */
public class PackagedWriter extends AbstractWriter {

    private final Map<String, List<TypeScriptFile>> packaged = new HashMap<>();
    public final int packageLengthLimit;
    public final String fallbackFileName;
    private int accepted = 0;

    public PackagedWriter(int minPackageLayers, String fallbackFileName) {
        if (minPackageLayers <= 0) {
            throw new IllegalArgumentException("'minPackageLayers' must be a positive number");
        }
        this.packageLengthLimit = minPackageLayers;
        this.fallbackFileName = fallbackFileName;
    }

    @Override
    public void accept(@NotNull TypeScriptFile file) {
        val cPath = file.path;
        val fileName = cPath.getParts().size() > packageLengthLimit
            ? String.join(".", cPath.getParts().subList(0, packageLengthLimit))
            : fallbackFileName;
        packaged.computeIfAbsent(fileName, k -> new ArrayList<>())
            .add(file);
        accepted += 1;
    }

    @Override
    protected void clearAcceptedFiles() {
        accepted = 0;
        packaged.clear();
    }

    @Override
    public int countAcceptedFiles() {
        return accepted;
    }

    @Override
    protected void writeClasses(Path base) throws IOException {
        for (val entry : packaged.entrySet()) {
            val fileName = entry.getKey();
            val files = entry.getValue();
            val filePath = base.resolve(fileName + suffix);
            if (Files.notExists(filePath)) {
                Files.createFile(filePath);
            }
            try (val writer = Files.newBufferedWriter(filePath)) {
                for (val file : files) {
                    writeFile(file, writer);
                    writer.write('\n');
                }
            }
        }
    }

    @Override
    protected void writeIndex(Path base) throws IOException {
        try (val writer = Files.newBufferedWriter(base.resolve(INDEX_FILE_NAME + suffix))) {
            for (val key : packaged.keySet()) {
                val refPath = key + suffix;
                writer.write(String.format("/// <reference path=%s />\n", ProbeJS.GSON.toJson(refPath)));
            }
        }
    }
}
