package zzzank.probejs.lang.decompiler;

import net.minecraftforge.fml.ModList;
import zzzank.probejs.ProbeJS;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class ProbeDecompiler {
    public static List<File> findModFiles() {
        return ModList
            .get()
            .getModFiles()
            .stream()
            .map(fileInfo -> fileInfo.getFile().getFilePath().toFile())
            .collect(Collectors.toList());
    }

    public final ProbeClassScanner scanner;

    public ProbeDecompiler() {
        this.scanner = new ProbeClassScanner();
    }

    public void addRuntimeSource(File source) {
        try {
            scanner.acceptFile(source);
        } catch (IOException e) {
            ProbeJS.LOGGER.error(String.format("Unable to load file: %s", source));
        }
    }

    public void fromMods() {
        try {
            for (File modFile : findModFiles()) {
                addRuntimeSource(modFile);
            }
        } catch (Exception e) {
            ProbeJS.LOGGER.error("Unable to load classes from class loader", e);
        }
    }
}
