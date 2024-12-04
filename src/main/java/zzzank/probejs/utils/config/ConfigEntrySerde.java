package zzzank.probejs.utils.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import lombok.AllArgsConstructor;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import zzzank.probejs.ProbeJS;
import zzzank.probejs.utils.CollectUtils;
import zzzank.probejs.utils.GameUtils;
import zzzank.probejs.utils.JsonUtils;

import java.util.*;
import java.util.function.Function;

/**
 * @author ZZZank
 */
@AllArgsConstructor
public class ConfigEntrySerde {

    public static final String DEFAULT_VALUE_KEY = "$default";
    public static final String VALUE_KEY = "$value";
    public static final String COMMENTS_KEY = "$comment";

    public final ConfigImpl source;

    public ConfigEntry<?> fromJson(String jsonName, JsonObject o) {
        try {
            val namespaced = source.ensureNamespace(jsonName);
            val namespace = namespaced.getKey();
            val name = namespaced.getValue();

            val reference = source.get(namespace, name);
            Function<JsonElement, Object> deserializer = reference == null
                ? JsonUtils::deserializeObject
                : obj -> ProbeJS.GSON.fromJson(obj, reference.expectedType);

            val defaultValue = deserializer.apply(o.get(DEFAULT_VALUE_KEY));
            val value = o.has(VALUE_KEY)
                ? deserializer.apply(o.get(VALUE_KEY))
                : defaultValue;
            val comments = extractComments(o);

            val entry = new ConfigEntry<>(
                source,
                namespace,
                name,
                reference == null ? defaultValue.getClass() : reference.expectedType,
                defaultValue,
                comments
            );
            entry.setNoSave(value);
            return entry;
        } catch (Exception e) {
            GameUtils.logThrowable(e);
        }
        return null;
    }

    private static @NotNull List<String> extractComments(JsonObject jsonObject) {
        val jsonComments = jsonObject.get(COMMENTS_KEY);
        if (jsonComments instanceof JsonPrimitive primitive) {
            return Collections.singletonList(primitive.getAsString());
        } else if (jsonComments instanceof JsonArray array) {
            val l = new ArrayList<String>(array.size());
            for (val element : array) {
                l.add(element.getAsString());
            }
            return l;
        }
        return Collections.emptyList();
    }

    private JsonElement valueToJson(ConfigEntry<?> entry, Object value) {
        if (value instanceof Enum<?> e) {
            return new JsonPrimitive(e.name());
        }
        return JsonUtils.parseObject(value);
    }

    public Map.Entry<String, JsonObject> toJson(ConfigEntry<?> entry) {
        val object = new JsonObject();

        object.add(DEFAULT_VALUE_KEY, valueToJson(entry, entry.defaultValue));
        object.add(VALUE_KEY, valueToJson(entry, entry.getRaw()));
        switch (entry.comments.size()) {
            case 0 -> {
            }
            case 1 -> object.add(COMMENTS_KEY, new JsonPrimitive(entry.comments.get(0)));
            default -> object.add(COMMENTS_KEY, JsonUtils.parseObject(entry.comments));
        }

        return CollectUtils.ofEntry(entry.namespace + '.' + entry.name, object);
    }
}
