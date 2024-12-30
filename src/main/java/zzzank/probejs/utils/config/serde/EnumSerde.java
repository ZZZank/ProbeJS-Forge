package zzzank.probejs.utils.config.serde;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.NotNull;

/**
 * @author ZZZank
 */
public class EnumSerde<T extends Enum<T>> extends GsonBasedSerde<T> {
    public EnumSerde(Gson gson, Class<T> expectedType) {
        super(gson, expectedType);
    }

    @Override
    public @NotNull JsonElement toJson(@NotNull T value) {
        return new JsonPrimitive(value.name());
    }
}
