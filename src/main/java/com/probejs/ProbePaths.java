package com.probejs;

import dev.latvian.kubejs.KubeJSPaths;
import dev.latvian.kubejs.util.UtilsJS;
import java.nio.file.Files;
import java.nio.file.Path;
import me.shedaniel.architectury.platform.Platform;

public abstract class ProbePaths {

    public static final Path CACHE = KubeJSPaths.EXPORTED;
    public static final Path PROBE = KubeJSPaths.DIRECTORY.resolve("probe");
    public static final Path DOCS = PROBE.resolve("docs");
    public static final Path GENERATED = PROBE.resolve("generated");
    public static final Path USER_DEFINED = PROBE.resolve("user");
    public static final Path WORKSPACE = Platform.getGameFolder().resolve(".vscode");

    public static void init() {
        if (Files.notExists(PROBE)) {
            UtilsJS.tryIO(() -> Files.createDirectories(PROBE));
        }
        if (Files.notExists(DOCS)) {
            UtilsJS.tryIO(() -> Files.createDirectories(DOCS));
        }
        if (Files.notExists(GENERATED)) {
            UtilsJS.tryIO(() -> Files.createDirectories(GENERATED));
        }
        if (Files.notExists(USER_DEFINED)) {
            UtilsJS.tryIO(() -> Files.createDirectories(USER_DEFINED));
        }
        if (Files.notExists(WORKSPACE)) {
            UtilsJS.tryIO(() -> Files.createDirectories(WORKSPACE));
        }
    }

    static {
        init();
    }
}
