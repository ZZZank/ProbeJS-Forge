package zzzank.probejs.utils.config.serde;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.latvian.kubejs.util.UtilsJS;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import zzzank.probejs.utils.JsonUtils;
import zzzank.probejs.utils.config.ConfigEntry;
import zzzank.probejs.utils.config.ConfigImpl;

/**
 * @author ZZZank
 */
public class ConfigImplSerde {
    public static final String DEFAULT_VALUE_KEY = "$default";
    public static final String VALUE_KEY = "$value";
    public static final String COMMENTS_KEY = "$comment";

    private final ConfigImpl source;

    public ConfigImplSerde(ConfigImpl attached) {
        this.source = attached;
    }

    public @NotNull JsonElement toJson(@NotNull ConfigImpl value) {
        val object = new JsonObject();
        for (val entry : value.entries()) {
            val o = new JsonObject();

            o.add(DEFAULT_VALUE_KEY, entry.serde.toJson(UtilsJS.cast(entry.getDefault())));
            o.add(VALUE_KEY, entry.serde.toJson(UtilsJS.cast(entry.get())));
            switch (entry.comments.size()) {
                case 0 -> {}
                case 1 -> o.add(COMMENTS_KEY, new JsonPrimitive(entry.comments.get(0)));
                default -> o.add(COMMENTS_KEY, JsonUtils.parseObject(entry.comments));
            }

            object.add(source.stripNamespace(entry.namespace, entry.name), o);
        }
        return object;
    }

    public void fromJson(@NotNull JsonElement json) {
        for (val entry : json.getAsJsonObject().entrySet()) {
            val namespaced = source.ensureNamespace(entry.getKey());
            val namespace = namespaced.getKey();
            val name = namespaced.getValue();

            val reference = (ConfigEntry<Object>) source.get(namespace, name);
            if (reference == null) {
                continue;
            }

            val raw = entry.getValue().getAsJsonObject().get(ConfigImplSerde.VALUE_KEY);
            reference.setNoSave(reference.serde.fromJson(raw));
        }
    }
}
