package zzzank.probejs.utils.config.serde;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.latvian.kubejs.util.UtilsJS;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import zzzank.probejs.ProbeJS;
import zzzank.probejs.utils.JsonUtils;
import zzzank.probejs.utils.config.ConfigEntry;
import zzzank.probejs.utils.config.ConfigImpl;

import java.util.Map;

/**
 * @author ZZZank
 */
public class ConfigImplSerde implements ConfigSerde<ConfigImpl> {
    public static final String DEFAULT_VALUE_KEY = "$default";
    public static final String VALUE_KEY = "$value";
    public static final String COMMENTS_KEY = "$comment";

    private final ConfigImpl source;

    public ConfigImplSerde(ConfigImpl attached) {
        this.source = attached;
    }

    @Override
    public @NotNull JsonElement toJson(@NotNull ConfigImpl value) {
        val object = new JsonObject();
        for (val entry : value.entries()) {
            val o = new JsonObject();

            o.add(DEFAULT_VALUE_KEY, entry.serde.toJson(UtilsJS.cast(entry.getDefault())));
            o.add(VALUE_KEY, entry.serde.toJson(UtilsJS.cast(entry.get())));
            if (entry.comments.size() == 1) {
                o.add(COMMENTS_KEY, new JsonPrimitive(entry.comments.get(0)));
            } else if (!entry.comments.isEmpty()) {
                o.add(COMMENTS_KEY, JsonUtils.parseObject(entry.comments));
            }

            object.add(entry.namespace + '.' + entry.name, o);
        }
        return object;
    }

    @Override
    public @NotNull ConfigImpl fromJson(@NotNull JsonElement json) {
        for (val entry : json.getAsJsonObject().entrySet()) {
            try {
                readSingle(entry);
            } catch (Exception e) {
                ProbeJS.LOGGER.error("Error when reading config entry: {}", entry.getKey(), e);
            }
        }
        return source;
    }

    private void readSingle(Map.Entry<String, JsonElement> entry) {
        val namespaced = source.ensureNamespace(entry.getKey());
        val namespace = namespaced.getKey();
        val name = namespaced.getValue();

        val reference = (ConfigEntry<Object>) source.get(namespace, name);
        if (reference == null) {
            return;
        }

        val raw = entry.getValue().getAsJsonObject().get(ConfigImplSerde.VALUE_KEY);
        reference.setNoSave(reference.serde.fromJson(raw));
    }
}
