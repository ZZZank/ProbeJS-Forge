package zzzank.probejs.utils.config;

import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.val;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

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

    public ConfigEntryBuilder<T> comment(@Nonnull String commentLine) {
        if (comments == null) {
            comments = new ArrayList<>();
        }
        val lines = Objects.requireNonNull(commentLine).split("\n");
        comments.addAll(Arrays.asList(lines));
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
