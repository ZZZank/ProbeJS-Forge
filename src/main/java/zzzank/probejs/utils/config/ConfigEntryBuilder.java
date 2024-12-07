package zzzank.probejs.utils.config;

import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import zzzank.probejs.utils.Asser;

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
    public Class<T> expectedType;
    public T defaultValue;
    public String namespace;
    public List<String> comments;

    public ConfigEntryBuilder(@NotNull ConfigImpl config, @NotNull String name) {
        this.root = config;
        this.name = name;
    }

    public <T_> ConfigEntryBuilder<T_> setDefault(Class<T_> type, T_ value) {
        val casted = (ConfigEntryBuilder<T_>) this;
        casted.expectedType = Asser.tNotNull(type, "config expected type");
        casted.defaultValue = Asser.tNotNull(value, "config default value");
        Asser.t(expectedType.isInstance(defaultValue), "config default value must match expected type");
        return casted;
    }

    public <T_> ConfigEntryBuilder<T_> setDefault(@Nonnull T_ defaultValue) {
        Asser.tNotNull(defaultValue, "config default value");
        Class<?> type;
        if (defaultValue instanceof Enum<?> e) {
            type = e.getDeclaringClass();
        } else {
            type = defaultValue.getClass();
        }
        return setDefault((Class<T_>) type, defaultValue);
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
        assert expectedType.isInstance(defaultValue);
        return this.root.merge(
            new ConfigEntry<>(this.root, namespace, name, expectedType, defaultValue, comments)
        );
    }
}
