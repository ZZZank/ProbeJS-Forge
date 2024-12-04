package zzzank.probejs.utils.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.datafixers.util.Pair;
import lombok.AllArgsConstructor;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import zzzank.probejs.ProbeJS;
import zzzank.probejs.utils.JsonUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

    public ConfigEntry<?> fromJson(String jsonName, JsonObject jsonObject) {
        try {
            val name$namespace = jsonName.lastIndexOf('.');
            val name = name$namespace < 0 ? jsonName : jsonName.substring(name$namespace + 1);
            val namespace = name$namespace < 0 ? source.defaultNamespace : jsonName.substring(0, name$namespace);

            val reference = source.get(namespace, name);
            Function<JsonElement, Object> deserializer = reference == null
                ? JsonUtils::deserializeObject
                : o -> ProbeJS.GSON.fromJson(o, reference.defaultValue.getClass());

            val defaultValue = deserializer.apply(jsonObject.get(DEFAULT_VALUE_KEY));
            val value = jsonObject.has(VALUE_KEY)
                ? deserializer.apply(jsonObject.get(VALUE_KEY))
                : defaultValue;
            val comments = extractComments(jsonObject);

            val entry = new ConfigEntry<>(source, name, defaultValue, namespace, comments);
            entry.setNoSave(value);
            return entry;
        } catch (Exception e) {
            ProbeJS.LOGGER.error(e);
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

    public Pair<String, JsonObject> toJson(ConfigEntry<?> entry) {
        val object = new JsonObject();

        val name = entry.namespace + '.' + entry.name;

        val value = valueToJson(entry, entry.getRaw());
        object.add(VALUE_KEY, value);

        switch (entry.comments.size()) {
            case 0 -> {
            }
            case 1 -> object.add(COMMENTS_KEY, new JsonPrimitive(entry.comments.get(0)));
            default -> object.add(COMMENTS_KEY, JsonUtils.parseObject(entry.comments));
        }

        val defaultValue = valueToJson(entry, entry.defaultValue);
        object.add(DEFAULT_VALUE_KEY, defaultValue);

        return new Pair<>(name, object);
    }
}
