package zzzank.probejs.plugin;

import dev.latvian.kubejs.KubeJSPlugin;
import dev.latvian.kubejs.util.KubeJSPlugins;
import dev.latvian.mods.rhino.util.HideFromJS;
import zzzank.probejs.ProbeJS;

import java.util.function.Consumer;

/**
 * A plugin for ProbeJS that is able to alter how ProbeJS works.
 * <br>
 * Different method calls might have same parameter/controller,
 * but it is advised to call different methods and their own stage
 * in order to prevent unexpected behavior.
 */
public class ProbeJSPlugin extends KubeJSPlugin implements ProbeDocPlugin, ProbeLifeCyclePlugin {

    @HideFromJS
    public static void forEachPlugin(Consumer<ProbeJSPlugin> consumer) {
        KubeJSPlugins.forEachPlugin(plugin -> {
            if (!(plugin instanceof ProbeJSPlugin probePlugin)) {
                return;
            }
            try {
                consumer.accept(probePlugin);
            } catch (Exception e) {
                ProbeJS.LOGGER.error("Error happened when applying ProbeJS plugin", e);
            }
        });
    }
}
