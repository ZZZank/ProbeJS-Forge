package moe.wolfgirl.probejs.lang.decompiler;

import lombok.Getter;
import org.jetbrains.java.decompiler.main.extern.IContextSource;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Getter
public class ProbeClassScanner {
    private final Set<Class<?>> scannedClasses = new HashSet<>();

    public void acceptFile(File file) throws IOException {
        try (var jarFile = new ZipFile(file)) {
            jarFile.stream()
                .filter(ZipEntry::isDirectory)
                .map(ZipEntry::getName)
                .filter(name -> name.endsWith(IContextSource.CLASS_SUFFIX))
                .map(name -> name.substring(0, name.length() - IContextSource.CLASS_SUFFIX.length()).replace("/", "."))
                //why, mojang
                .filter(name -> !name.contains("com.mojang.blaze3d.systems.TimerQuery"))
                .forEach(name -> {
                    try {
                        scannedClasses.add(Class.forName(name));
                    } catch (ClassNotFoundException ignored) {
                    }
                });
        }
    }
}
