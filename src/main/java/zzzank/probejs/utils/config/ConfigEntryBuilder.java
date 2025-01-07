package zzzank.probejs.utils.config;

import lombok.val;
import org.jetbrains.annotations.NotNull;
import zzzank.probejs.ProbeJS;
import zzzank.probejs.utils.Asser;
import zzzank.probejs.utils.Cast;
import zzzank.probejs.utils.config.binding.ConfigBinding;
import zzzank.probejs.utils.config.binding.DefaultBinding;
import zzzank.probejs.utils.config.binding.RangedBinding;
import zzzank.probejs.utils.config.binding.ReadOnlyBinding;
import zzzank.probejs.utils.config.serde.ConfigSerde;
import zzzank.probejs.utils.config.serde.DefaultSerde;
import zzzank.probejs.utils.config.serde.EnumSerde;

import java.util.*;

/**
 * @author ZZZank
 */
public class ConfigEntryBuilder<T> {

    @NotNull
    public final ConfigImpl root;
    @NotNull
    public String name;
    @NotNull
    public String namespace;
    protected Class<T> expectedType;
    public List<String> comments;
    public ConfigBinding<T> binding;
    public ConfigSerde<T> serde;

    protected ConfigEntryBuilder(@NotNull ConfigImpl config, @NotNull String namespace, @NotNull String name) {
        this.root = config;
        this.name = name;
        this.namespace = namespace;
    }

    public ConfigEntryBuilder<T> setName(@NotNull String name) {
        this.name = Asser.tNotNull(name, "config entry name");
        return this;
    }

    public ConfigEntryBuilder<T> setNamespace(@NotNull String namespace) {
        this.namespace = Asser.tNotNull(namespace, "config entry namespace");
        return this;
    }

    public <T_> ConfigEntryBuilder<T_> setDefault(Class<T_> type, ConfigBinding<T_> binding) {
        Asser.t(type.isInstance(binding.getDefault()), "config default value must match expected type");
        val casted = Cast.<ConfigEntryBuilder<T_>>to(this);
        casted.expectedType = Asser.tNotNull(type, "config expected type");
        casted.binding = Asser.tNotNull(binding, "config binding");
        return casted;
    }

    private <T_> ConfigEntryBuilder<T_> setDefault(ConfigBinding<T_> binding) {
        return setDefault(binding.clazzFromDefaultValue(), binding);
    }

    public <T_> ConfigEntryBuilder<T_> setDefault(@NotNull T_ defaultValue) {
        return setDefault(new DefaultBinding<>(defaultValue, name));
    }

    public <T_> ConfigEntryBuilder<T_> readOnly(@NotNull T_ defaultValue) {
        return setDefault(new ReadOnlyBinding<>(defaultValue, name));
    }

    public <T_ extends Comparable<T_>> ConfigEntryBuilder<T_> setDefault(
        @NotNull T_ defaultValue,
        @NotNull T_ min,
        @NotNull T_ max
    ) {
        return setDefault(new RangedBinding<>(defaultValue, name, min, max));
    }

    public ConfigEntryBuilder<T> setSerde(ConfigSerde<T> serde) {
        this.serde = serde;
        return this;
    }

    public ConfigEntryBuilder<T> comment(String... comments) {
        if (this.comments == null) {
            this.comments = new ArrayList<>();
        }
        for (val comment : comments) {
            this.comments.addAll(Arrays.asList(comment.split("\n")));
        }
        return this;
    }

    public ConfigEntryBuilder<T> setComments(List<String> comments) {
        this.comments = Objects.requireNonNull(comments);
        return this;
    }

    public ConfigEntry<T> build() {
        if (comments == null) {
            comments = Collections.emptyList();
        }
        if (serde == null) {
            if (Enum.class.isAssignableFrom(expectedType)) {
                serde = new EnumSerde(ProbeJS.GSON, expectedType);
            } else {
                serde = new DefaultSerde<>(ProbeJS.GSON, expectedType);
            }
        }
        return this.root.register(
            new ConfigEntry<>(this.root, namespace, name, serde, binding, comments)
        );
    }
}
