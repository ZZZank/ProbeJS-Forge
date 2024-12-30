package zzzank.probejs.utils.config;

import com.google.common.collect.ImmutableList;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import zzzank.probejs.ProbeJS;
import zzzank.probejs.utils.Asser;
import zzzank.probejs.utils.config.binding.ConfigBinding;
import zzzank.probejs.utils.config.serde.ConfigSerde;

import java.util.List;

/**
 * @author ZZZank
 */
public class ConfigEntry<T> {

    public final ConfigImpl source;
    public final String namespace;
    public final String name;

    public final ConfigSerde<T> serde;
    public final ConfigBinding<T> binding;
    public final ImmutableList<String> comments;

    public ConfigEntry(
        ConfigImpl source,
        String namespace,
        String name,
        ConfigSerde<T> serde,
        ConfigBinding<T> binding,
        List<String> comments
    ) {
        this.source = Asser.tNotNull(source, "source");
        this.name = Asser.tNotNull(name, "name");
        this.serde = Asser.tNotNull(serde, "serde");
        this.binding = Asser.tNotNull(binding, "defaultValue");
        this.namespace = Asser.tNotNull(namespace, "namespace");
        this.comments = ImmutableList.copyOf(Asser.tNotNullAll(comments, "comments"));
    }

    /**
     * `null` will be redirected to default value
     */
    public void set(T value) {
        setNoSave(value);
        source.save();
    }

    public void reset() {
        binding.reset();
        source.save();
    }

    public void setNoSave(T value) {
        val report = binding.set(value);
        if (report.hasError()) {
            ProbeJS.LOGGER.error(report.asException());
        }
    }

    public T get() {
        return binding.get();
    }

    @NotNull
    public T getDefault() {
        return binding.getDefault();
    }
}
