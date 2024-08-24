package zzzank.probejs.utils.config;

import com.google.common.collect.ImmutableList;
import zzzank.probejs.ProbeJS;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author ZZZank
 */
public class ConfigEntry<T> {
    public final ConfigImpl source;
    public final String name;
    public final T defaultValue;
    private T value;
    public final String namespace;
    public final ImmutableList<String> comments;

    public ConfigEntry(String name, @Nonnull T defaultValue) {
        this(null, name, defaultValue, ProbeJS.MOD_ID, Collections.emptyList());
    }

    public ConfigEntry(ConfigImpl source, String name, T defaultValue, String namespace, List<String> comments) {
        this.source = Objects.requireNonNull(source);
        this.name = Objects.requireNonNull(name);
        this.defaultValue = Objects.requireNonNull(defaultValue);
        this.namespace = Objects.requireNonNull(namespace);
        this.comments = ImmutableList.copyOf(Objects.requireNonNull(comments));
    }

    public void set(T value) {
        if (value == null) {
            value = defaultValue;
        }
        if (Objects.equals(this.value, value)) {
            return;
        }
        this.value = value;
        source.save();
    }

    public T getRaw() {
        return value;
    }

    public T get() {
        if (value == null) {
            set(defaultValue);
        }
        return value;
    }


}
