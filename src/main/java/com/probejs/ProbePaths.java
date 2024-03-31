package com.probejs;

import dev.latvian.kubejs.KubeJSPaths;
import dev.latvian.kubejs.util.UtilsJS;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import me.shedaniel.architectury.platform.Platform;

public class ProbePaths {

    public static final Path CACHE = KubeJSPaths.EXPORTED;
    public static final Path PROBE = KubeJSPaths.DIRECTORY.resolve("probe");
    public static final Path DOCS = PROBE.resolve("docs");
    public static final Path GENERATED = PROBE.resolve("generated");
    public static final Path USER_DEFINED = PROBE.resolve("user");
    public static final Path GAME_FOLDER = Platform.getGameFolder();
    public static final Path WORKSPACE = GAME_FOLDER.resolve(".vscode");

    public static void init() {
        if (Files.notExists(PROBE, new LinkOption[0])) {
            UtilsJS.tryIO(() -> Files.createDirectories(PROBE));
        }
        if (Files.notExists(DOCS, new LinkOption[0])) {
            UtilsJS.tryIO(() -> Files.createDirectories(DOCS));
        }
        if (Files.notExists(GENERATED, new LinkOption[0])) {
            UtilsJS.tryIO(() -> Files.createDirectories(GENERATED));
        }
        if (Files.notExists(USER_DEFINED, new LinkOption[0])) {
            UtilsJS.tryIO(() -> Files.createDirectories(USER_DEFINED));
        }
        if (Files.notExists(WORKSPACE, new LinkOption[0])) {
            UtilsJS.tryIO(() -> Files.createDirectories(WORKSPACE));
        }
    }

    static {
        init();
    }
}
