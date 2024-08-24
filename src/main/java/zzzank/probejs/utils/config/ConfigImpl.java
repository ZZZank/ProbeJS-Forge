package zzzank.probejs.utils.config;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import lombok.val;
import zzzank.probejs.ProbeJS;

import java.nio.file.Path;

/**
 * @author ZZZank
 */
public class ConfigImpl {

    public final String defaultNamespace;
    private final Table<String, String, ConfigEntry<?>> all;
    private final Path path;

    public ConfigImpl(Path path, String defaultNamespace) {
        this.path = path;
        all = HashBasedTable.create();
        this.defaultNamespace = defaultNamespace;
    }

    public void initFromFile() {
    }

    public void save() {
    }

    public ConfigEntry<?> get(String name) {
        return get(defaultNamespace, name);
    }

    public ConfigEntry<?> get(String namespace, String name) {
        return all.get(namespace, name);
    }

    public <T> ConfigEntry<T> addConfig(ConfigEntryBuilder<T> builder) {
        val configEntry = builder.build(this);
        all.put(builder.namespace, builder.name, configEntry);
        return configEntry;
    }
}
