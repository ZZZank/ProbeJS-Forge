package moe.wolfgirl.probejs;

import dev.latvian.kubejs.KubeJSPaths;
import dev.latvian.kubejs.util.UtilsJS;
import me.shedaniel.architectury.platform.Platform;

import java.nio.file.Files;
import java.nio.file.Path;

public class ProbePaths {

    public static Path PROBE = Platform.getGameFolder().resolve(".probe");
    public static Path WORKSPACE_SETTINGS = Platform.getGameFolder().resolve(".vscode");
    public static Path SETTINGS_JSON = KubeJSPaths.CONFIG.resolve("probe-settings.json");
    public static Path VSCODE_JSON = WORKSPACE_SETTINGS.resolve("settings.json");
    public static Path GIT_IGNORE = Platform.getGameFolder().resolve(".gitignore");
    public static Path DECOMPILED = PROBE.resolve("decompiled");

    public static void init() {
        if (Files.notExists(PROBE)) {
            UtilsJS.tryIO(() -> Files.createDirectories(PROBE));
        }
        if (Files.notExists(WORKSPACE_SETTINGS)) {
            UtilsJS.tryIO(() -> Files.createDirectories(WORKSPACE_SETTINGS));
        }
        if (Files.notExists(ProbePaths.DECOMPILED)) {
            UtilsJS.tryIO(() -> Files.createDirectories(ProbePaths.DECOMPILED));
        }
    }

    static {
        init();
    }
}
