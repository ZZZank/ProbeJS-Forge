package zzzank.probejs.utils.config;

import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * @author ZZZank
 */
@Setter
@Accessors(chain = true)
public class ConfigEntryBuilder<T> {

    @NotNull
    public final ConfigImpl root;
    @Nonnull
    public final String name;
    @Nonnull
    public T defaultValue;
    @Nullable
    public String namespace;
    @Nullable
    public List<String> comments;

    public ConfigEntryBuilder(@NotNull ConfigImpl config, @NotNull String name) {
        this.root = config;
        this.name = name;
    }

    public <T_> ConfigEntryBuilder<T_> setDefaultValue(@Nonnull T_ defaultValue) {
        val casted = (ConfigEntryBuilder<T_>) this;
        casted.defaultValue = Objects.requireNonNull(defaultValue);
        return casted;
    }

    public ConfigEntryBuilder<T> comment(@Nonnull String comment) {
        return comments(Objects.requireNonNull(comment.split("\n")));
    }

    public ConfigEntryBuilder<T> comments(String... comments) {
        if (this.comments == null) {
            this.comments = new ArrayList<>();
        }
        for (val comment : comments) {
            comment(comment);
        }
        return this;
    }

    public ConfigEntry<T> build() {
        if (namespace == null) {
            namespace = this.root.defaultNamespace;
        }
        if (comments == null) {
            comments = Collections.emptyList();
        }
        return new ConfigEntry<>(this.root, name, defaultValue, namespace, comments);
    }
}
