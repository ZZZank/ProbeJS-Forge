package zzzank.probejs.plugin;

import dev.latvian.kubejs.util.KubeJSPlugins;
import dev.latvian.mods.rhino.util.HideFromJS;
import lombok.val;
import zzzank.probejs.ProbeJS;
import zzzank.probejs.docs.ProbeBuiltinDocs;
import zzzank.probejs.utils.GameUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author ZZZank
 */
public class ProbeJSPlugins {

    private static final List<ProbeJSPlugin> ALL = new ArrayList<>();
    private static boolean initialized = false;

    public static void init() {
        if (initialized) {
            return;
        }
        register(ProbeBuiltinDocs.get());
        collectFromKubeJS();
        initialized = true;
    }

    public static void register(ProbeJSPlugin plugin) {
        ALL.add(plugin);
    }

    public static List<ProbeJSPlugin> getAll() {
        return Collections.unmodifiableList(ALL);
    }

    private static void collectFromKubeJS() {
        KubeJSPlugins.forEachPlugin(kubeJSPlugin -> {
            if (kubeJSPlugin instanceof ProbeJSPlugin probeJSPlugin) {
                ALL.add(probeJSPlugin);
            }
        });
    }

    @HideFromJS
    public static void forEachPlugin(Consumer<ProbeJSPlugin> action) {
        for (val plugin : ALL) {
            try {
                action.accept(plugin);
            } catch (Exception e) {
                ProbeJS.LOGGER.error("Error happened when applying ProbeJS plugin: {}", plugin.getClass().getName());
                GameUtils.logThrowable(e);
            }
        }
    }
}
