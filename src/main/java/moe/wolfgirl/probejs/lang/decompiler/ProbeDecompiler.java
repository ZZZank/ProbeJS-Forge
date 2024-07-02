package moe.wolfgirl.probejs.lang.decompiler;

import moe.wolfgirl.probejs.ProbeJS;
import net.minecraftforge.fml.ModList;
import org.jetbrains.java.decompiler.main.Fernflower;
import org.jetbrains.java.decompiler.main.extern.IFernflowerPreferences;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class ProbeDecompiler {
    public static List<File> findModFiles() {
        return ModList
            .get()
            .getModFiles()
            .stream()
            .map(fileInfo -> fileInfo.getFile().getFilePath())
            .map(Path::toFile)
            .collect(Collectors.toList());
    }

    public final ProbeFileSaver resultSaver;
    public final ProbeClassScanner scanner;

    public ProbeDecompiler() {
        this.resultSaver = new ProbeFileSaver();
        this.scanner = new ProbeClassScanner();
    }

    public void addRuntimeSource(File source) {
        try {
            scanner.acceptFile(source);
        } catch (IOException e) {
            ProbeJS.LOGGER.error("Unable to load file: %s".formatted(source));
        }
    }

    public void fromMods() {
        for (File modFile : findModFiles()) {
            addRuntimeSource(modFile);
        }
    }

    public void decompileContext() {
        HashMap<String, Object> prop = new HashMap<>();
        prop.put(IFernflowerPreferences.RENAME_ENTITIES, "1");
        prop.put(IFernflowerPreferences.USER_RENAMER_CLASS, ProbeRemapper.class.getName();

        Fernflower engine = new Fernflower(resultSaver, prop, new ProbeDecompilerLogger());
        ProbeClassSource source = new ProbeClassSource(scanner.getScannedClasses());
        engine.addSource(source);

        resultSaver.classCount = 0;
        try {
            engine.decompileContext();
        } finally {
            engine.clearContext();
        }
    }
}
