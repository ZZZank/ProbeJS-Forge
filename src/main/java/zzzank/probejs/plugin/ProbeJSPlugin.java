package zzzank.probejs.plugin;

import dev.latvian.kubejs.KubeJSPlugin;

/**
 * A plugin for ProbeJS that is able to alter how ProbeJS works.
 * <br>
 * Different method calls might have same parameter/controller,
 * but it is advised to call different methods and their own stage
 * in order to prevent unexpected behavior.
 */
public class ProbeJSPlugin
    extends KubeJSPlugin
    implements ProbeDocPlugin, ProbeLifeCyclePlugin {
}
