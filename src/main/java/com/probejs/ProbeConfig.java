package com.probejs;

import com.google.gson.GsonBuilder;
import dev.latvian.kubejs.KubeJSPaths;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class ProbeConfig {

    private static ProbeConfig reference = null;
    private static final Path CONFIG = KubeJSPaths.CONFIG.resolve("probejs.json");
    public boolean keepBeaned = true;
    public boolean disabled = false;
    public boolean vanillaOrder = true;
    public boolean exportClassNames = false;
    public boolean trimming = true;

    @SuppressWarnings("unchecked")
    private static <E> E fetchPropertyOrDefault(Object key, Map<?, ?> values, E defaultValue) {
        Object v = values.get(key);
        return v == null ? defaultValue : (E) v;
    }

    public static ProbeConfig getInstance() {
        if (reference == null) {
            reference = new ProbeConfig();
        }
        return reference;
    }

    private ProbeConfig() {
        Path cfg = KubeJSPaths.CONFIG.resolve("probejs.json");
        if (Files.exists(cfg)) {
            try {
                Map<?, ?> obj = ProbeJS.GSON.fromJson(Files.newBufferedReader(cfg), Map.class);
                keepBeaned = fetchPropertyOrDefault("keepBeaned", obj, true);
                disabled = fetchPropertyOrDefault("disabled", obj, false);
                vanillaOrder = fetchPropertyOrDefault("vanillaOrder", obj, true);
                exportClassNames = fetchPropertyOrDefault("exportClassNames", obj, false);
                trimming = fetchPropertyOrDefault("trimming", obj, false);
            } catch (IOException e) {
                ProbeJS.LOGGER.warn("Cannot read config properties, falling back to defaults.");
            }
        }
    }

    public void save() {
        try (BufferedWriter writer = Files.newBufferedWriter(CONFIG)) {
            new GsonBuilder().setPrettyPrinting().create().toJson(this, writer);
        } catch (IOException e) {
            ProbeJS.LOGGER.warn("Cannot write config, settings are not saved.");
        }
    }
}
