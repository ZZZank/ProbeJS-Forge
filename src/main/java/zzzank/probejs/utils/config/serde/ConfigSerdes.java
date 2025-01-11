package zzzank.probejs.utils.config.serde;

import lombok.experimental.UtilityClass;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import zzzank.probejs.ProbeJS;
import zzzank.probejs.utils.Cast;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * @author ZZZank
 */
@UtilityClass
public class ConfigSerdes {

    private final Map<Class<?>, ConfigSerde<?>> REGISTERED = new IdentityHashMap<>();

    static {
        register(Pattern.class, new PatternSerde());
    }

    public <T> void register(@NotNull Class<T> type, @NotNull ConfigSerde<T> serde) {
        REGISTERED.put(Objects.requireNonNull(type), Objects.requireNonNull(serde));
    }

    public <T> ConfigSerde<T> get(T value) {
        if (value == null) {
            return null;
        }
        val type = Cast.<Class<T>>to(value.getClass());
        val got = REGISTERED.get(type);
        if (got != null) {
            return Cast.to(got);
        }
        if (value instanceof Enum<?> e) {
            return Cast.to(new EnumSerde<>(ProbeJS.GSON, e.getDeclaringClass()));
        }
        return new DefaultSerde<>(ProbeJS.GSON, type);
    }
}
