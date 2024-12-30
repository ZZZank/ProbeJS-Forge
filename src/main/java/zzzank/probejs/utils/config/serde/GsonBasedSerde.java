package zzzank.probejs.utils.config.serde;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import org.jetbrains.annotations.NotNull;

/**
 * @author ZZZank
 */
public class GsonBasedSerde<T> implements ConfigSerde<T> {
    protected final Gson gson;
    protected final Class<T> expectedType;

    public GsonBasedSerde(Gson gson, Class<T> expectedType) {
        this.gson = gson;
        this.expectedType = expectedType;
    }

    @Override
    public @NotNull JsonElement toJson(@NotNull T value) {
        return gson.toJsonTree(value, expectedType);
    }

    @Override
    public @NotNull T fromJson(@NotNull JsonElement json) {
        return gson.fromJson(json, expectedType);
    }
}
