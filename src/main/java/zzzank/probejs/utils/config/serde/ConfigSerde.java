package zzzank.probejs.utils.config.serde;

import com.google.gson.JsonElement;
import org.jetbrains.annotations.NotNull;

/**
 * @param <T> object type at runtime
 * @author ZZZank
 */
public interface ConfigSerde<T> {

    @NotNull
    JsonElement toJson(@NotNull T value);

    @NotNull
    T fromJson(@NotNull JsonElement json);
}
