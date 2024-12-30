package zzzank.probejs.utils.config.serde;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import org.jetbrains.annotations.NotNull;
import zzzank.probejs.utils.JsonUtils;

/**
 * @author ZZZank
 */
public class DefaultSerde<T> extends GsonBasedSerde<T> {
    public DefaultSerde(Gson gson, Class<T> expectedType) {
        super(gson, expectedType);
    }

    @Override
    public @NotNull JsonElement toJson(@NotNull T value) {
        return JsonUtils.parseObject(value);
    }
}
