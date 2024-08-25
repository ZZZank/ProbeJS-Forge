package zzzank.probejs.utils.config;

import lombok.Setter;
import lombok.experimental.Accessors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author ZZZank
 */
@Setter
@Accessors(chain = true)
public class ConfigEntryBuilder<T> {
    @Nonnull
    public T defaultValue;
    @Nullable
    public String namespace;
    @Nonnull
    public String name;
    @Nullable
    public List<String> comments;

    public static <T> ConfigEntryBuilder<T> of(T defaultValue) {
        return new ConfigEntryBuilder<T>().setDefaultValue(Objects.requireNonNull(defaultValue));
    }

    public static <T> ConfigEntryBuilder<T> of(String name, T defaultValue) {
        return of(defaultValue).setName(Objects.requireNonNull(name));
    }

    public ConfigEntryBuilder<T> comment(String commentLine) {
        if (comments == null) {
            comments = new ArrayList<>();
        }
        comments.add(commentLine);
        return this;
    }

    public ConfigEntry<T> build(ConfigImpl source) {
        if (namespace == null) {
            namespace = source.defaultNamespace;
        }
        if (comments == null) {
            comments = Collections.emptyList();
        }
        return new ConfigEntry<>(source, name, defaultValue, namespace, comments);
    }
}
