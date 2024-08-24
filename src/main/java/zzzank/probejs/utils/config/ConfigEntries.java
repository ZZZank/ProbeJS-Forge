package zzzank.probejs.utils.config;

import com.mojang.datafixers.util.Either;
import lombok.AllArgsConstructor;
import lombok.val;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ZZZank
 */
@AllArgsConstructor
public class ConfigEntries {

    private final Either<ConfigEntry<?>, Map<String, ConfigEntry<?>>> data;

    public static ConfigEntries wrap(ConfigEntry<?> configEntry) {
        return new ConfigEntries(Either.left(configEntry));
    }

    public static ConfigEntries wrap(Map<String, ConfigEntry<?>> map) {
        return new ConfigEntries(Either.right(map));
    }

    public static ConfigEntries create() {
        return new ConfigEntries(Either.right(new HashMap<>()));
    }

    public boolean isSingle() {
        return data.left().isPresent();
    }

    public boolean isMulti() {
        return data.right().isPresent();
    }

    public ConfigEntry<?> getSingle() {
        return data.orThrow();
    }

    public ConfigEntry<?> getMulti(String name) {
        val right = data.right();
        if (!right.isPresent()) {
            return null; // todo: throw?
        }
        return right.get().get(name);
    }
}
