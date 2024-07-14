package moe.wolfgirl.probejs.lang.decompiler;

import lombok.val;
import moe.wolfgirl.probejs.ProbeJS;
import net.minecraftforge.fml.ModList;
import org.jetbrains.java.decompiler.main.Fernflower;
import org.jetbrains.java.decompiler.main.extern.IFernflowerPreferences;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
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
            ProbeJS.LOGGER.error(String.format("Unable to load file: %s", source));
        }
    }

    public void fromMods() {
//        for (File modFile : findModFiles()) {
//            addRuntimeSource(modFile);
//        }
        try {
            scanner.fromClassLoader();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            ProbeJS.LOGGER.error("Unable to load classes from class loader", e);
        }
    }

    public void decompileContext() {
        HashMap<String, Object> prop = new HashMap<>();

        prop.put(IFernflowerPreferences.RENAME_ENTITIES, "1");
        prop.put(IFernflowerPreferences.USER_RENAMER_CLASS, ProbeRemapper.class.getName());

        val engine = new Fernflower(resultSaver, prop, new ProbeDecompilerLogger());
        val source = new ProbeClassSource(scanner.getScannedClasses());
        engine.addSource(source);

        resultSaver.classCount = 0;
        try {
            engine.decompileContext();
        } finally {
            engine.clearContext();
        }
    }
}
