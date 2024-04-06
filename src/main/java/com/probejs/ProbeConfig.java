package com.probejs;

import com.google.gson.GsonBuilder;
import com.probejs.util.PUtil;
import dev.latvian.kubejs.KubeJSPaths;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/**
 * Implementations of ProbeJS config system. For config instance ProbeJS is using,
 * see {@code ProbeJS.CONFIG}
 * @see com.probejs.ProbeJS#CONFIG
 */
public class ProbeConfig {

    private static ProbeConfig reference = null;
    public static final Path PATH = KubeJSPaths.CONFIG.resolve("probejs.json");
    public boolean keepBeaned = true;
    public boolean enabled = true;
    public boolean vanillaOrder = true;
    public boolean exportClassNames = false;
    public boolean trimming = true;

    public static ProbeConfig getInstance() {
        if (reference == null) {
            ProbeConfig.reference = new ProbeConfig();
        }
        return ProbeConfig.reference;
    }

    private ProbeConfig() {
        Path cfg = KubeJSPaths.CONFIG.resolve("probejs.json");
        if (Files.exists(cfg)) {
            try {
                Map<?, ?> obj = ProbeJS.GSON.fromJson(Files.newBufferedReader(cfg), Map.class);
                keepBeaned = PUtil.castedGetOrDef("keepBeaned", obj, true);
                enabled = PUtil.castedGetOrDef("enabled", obj, true);
                vanillaOrder = PUtil.castedGetOrDef("vanillaOrder", obj, true);
                exportClassNames = PUtil.castedGetOrDef("exportClassNames", obj, false);
                trimming = PUtil.castedGetOrDef("trimming", obj, false);

                Object cfgDisabled = obj.get("disabled");
                if (cfgDisabled != null) {
                    this.enabled = !(boolean) cfgDisabled;
                    save();
                }
            } catch (IOException e) {
                ProbeJS.LOGGER.warn("Cannot read config properties, falling back to defaults.");
            }
        }
    }

    public void save() {
        try (BufferedWriter writer = Files.newBufferedWriter(PATH)) {
            new GsonBuilder().setPrettyPrinting().create().toJson(this, writer);
        } catch (IOException e) {
            ProbeJS.LOGGER.warn("Cannot write config, settings are not saved.");
        }
    }
}
