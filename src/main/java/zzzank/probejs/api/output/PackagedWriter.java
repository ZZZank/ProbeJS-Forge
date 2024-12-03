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

    public PackagedWriter(int packageLengthMin, String fallbackFileName) {
        if (packageLengthMin <= 0) {
            throw new IllegalArgumentException("'packageLengthMin' must be a positive number");
        }
        this.packageLengthLimit = packageLengthMin;
        this.fallbackFileName = fallbackFileName;
    }

    @Override
    public void accept(@NotNull TypeScriptFile file) {
        val cPath = file.path;
        val fileName = cPath.parts.length > packageLengthLimit
            ? String.join(".", Arrays.asList(cPath.parts).subList(0, packageLengthLimit))
            : fallbackFileName;
        packaged.computeIfAbsent(fileName, k -> new ArrayList<>())
            .add(file);
    }

    @Override
    protected void clearFiles() {
        packaged.clear();
    }

    @Override
    protected void writeClasses(Path base) throws IOException {
        for (val entry : packaged.entrySet()) {
            val fileName = entry.getKey();
            val files = entry.getValue();
            val filePath = base.resolve(fileName + D_TS_SUFFIX);
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
        try (val writer = Files.newBufferedWriter(base.resolve(INDEX_FILE_NAME))) {
            for (val key : packaged.keySet()) {
                val refPath = key + D_TS_SUFFIX;
                writer.write(String.format("/// <reference path=%s />\n", ProbeJS.GSON.toJson(refPath)));
            }
        }
    }
}
