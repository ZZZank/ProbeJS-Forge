package zzzank.probejs.utils.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.datafixers.util.Pair;
import lombok.AllArgsConstructor;
import lombok.val;
import zzzank.probejs.ProbeJS;
import zzzank.probejs.utils.JsonUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
            val namespace = name$namespace < 0 ? null : jsonName.substring(0, name$namespace);
            val defaultValue = JsonUtils.deserializeObject(jsonObject.get(DEFAULT_VALUE_KEY));
            val value = jsonObject.has(VALUE_KEY)
                ? JsonUtils.deserializeObject(jsonObject.get(VALUE_KEY))
                : defaultValue;
            List<String> comments;
            val jsonComments = jsonObject.get(COMMENTS_KEY);
            if (jsonComments instanceof JsonPrimitive primitive) {
                comments = Collections.singletonList(primitive.getAsString());
            } else if (jsonComments instanceof JsonArray array) {
                val l = new ArrayList<String>(array.size());
                for (val element : array) {
                    l.add(element.getAsString());
                }
                comments = l;
            } else {
                comments = Collections.emptyList();
            }

            val entry = new ConfigEntry<>(source, name, defaultValue, namespace, comments);
            entry.set(value);
            return entry;
        } catch (Exception e) {
            ProbeJS.LOGGER.error(e);
        }
        return null;
    }

    public Pair<String, JsonObject> toJson(ConfigEntry<?> entry) {
        val object = new JsonObject();

        val name = entry.namespace + '.' + entry.name;

        val value = JsonUtils.parseObject(entry.getRaw());
        object.add(VALUE_KEY, value);

        switch (entry.comments.size()) {
            case 0 -> {}
            case 1 -> object.add(COMMENTS_KEY, new JsonPrimitive(entry.comments.get(0)));
            default -> object.add(COMMENTS_KEY, JsonUtils.parseObject(entry.comments));
        }

        val defaultValue = JsonUtils.parseObject(entry.defaultValue);
        object.add(DEFAULT_VALUE_KEY, defaultValue);

        return new Pair<>(name, object);
    }
}
