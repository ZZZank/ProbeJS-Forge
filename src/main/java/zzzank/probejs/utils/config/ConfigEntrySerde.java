package zzzank.probejs.utils.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import lombok.AllArgsConstructor;
import lombok.val;
import zzzank.probejs.utils.CollectUtils;
import zzzank.probejs.utils.JsonUtils;

import java.util.*;

/**
 * @author ZZZank
 */
@AllArgsConstructor
public class ConfigEntrySerde {

    public static final String DEFAULT_VALUE_KEY = "$default";
    public static final String VALUE_KEY = "$value";
    public static final String COMMENTS_KEY = "$comment";

    public final ConfigImpl source;

    private JsonElement valueToJson(ConfigEntry<?> entry, Object value) {
        if (value instanceof Enum<?> e) {
            return new JsonPrimitive(e.name());
        }
        return JsonUtils.parseObject(value);
    }

    public Map.Entry<String, JsonObject> toJson(ConfigEntry<?> entry) {
        val object = new JsonObject();

        object.add(DEFAULT_VALUE_KEY, valueToJson(entry, entry.getDefault()));
        object.add(VALUE_KEY, valueToJson(entry, entry.get()));
        switch (entry.comments.size()) {
            case 0 -> {
            }
            case 1 -> object.add(COMMENTS_KEY, new JsonPrimitive(entry.comments.get(0)));
            default -> object.add(COMMENTS_KEY, JsonUtils.parseObject(entry.comments));
        }

        return CollectUtils.ofEntry(entry.namespace + '.' + entry.name, object);
    }
}
