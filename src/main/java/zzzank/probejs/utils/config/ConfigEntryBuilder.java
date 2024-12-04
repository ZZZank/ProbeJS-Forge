package zzzank.probejs.utils.config;

import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
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
    public Class<?> expectedType;
    public T defaultValue;
    public String namespace;
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
        if (this.comments == null) {
            this.comments = new ArrayList<>();
        }
        comments.addAll(Arrays.asList(comment.split("\n")));
        return this;
    }

    public ConfigEntryBuilder<T> comments(String... comments) {
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
        if (expectedType == null) {
            if (defaultValue instanceof Enum<?>) {
                expectedType = ((Enum<?>) defaultValue).getDeclaringClass();
            } else {
                expectedType = defaultValue.getClass();
            }
        }
        assert expectedType.isInstance(defaultValue);
        return this.root.merge(
            new ConfigEntry<>(this.root, namespace, name, expectedType, defaultValue, comments)
        );
    }
}
