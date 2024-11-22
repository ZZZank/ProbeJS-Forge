package zzzank.probejs.plugin;

import dev.latvian.mods.rhino.util.HideFromJS;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import zzzank.probejs.ProbeJS;
import zzzank.probejs.utils.GameUtils;

import java.util.*;
import java.util.function.Consumer;

/**
 * @author ZZZank
 */
public class ProbeJSPlugins {

    private static final List<ProbeJSPlugin> ALL = new ArrayList<>();

    public static void register(@NotNull ProbeJSPlugin... plugins) {
        for (val plugin : plugins) {
            ALL.add(Objects.requireNonNull(plugin));
        }
    }

    public static List<ProbeJSPlugin> getAll() {
        return Collections.unmodifiableList(ALL);
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
