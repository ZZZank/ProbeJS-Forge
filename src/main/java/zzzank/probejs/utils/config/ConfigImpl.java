package zzzank.probejs.utils.config;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.gson.JsonObject;
import lombok.val;
import zzzank.probejs.ProbeJS;
import zzzank.probejs.utils.Asser;
import zzzank.probejs.utils.Cast;
import zzzank.probejs.utils.CollectUtils;
import zzzank.probejs.utils.config.serde.ConfigImplSerde;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * @author ZZZank
 */
public class ConfigImpl {

    public final String defaultNamespace;
    public final ConfigImplSerde serde;
    private final Table<String, String, ConfigEntry<?>> all;
    public final Path path;

    public ConfigImpl(Path path, String defaultNamespace) {
        this.path = path;
        this.serde = new ConfigImplSerde(this);
        all = HashBasedTable.create();
        this.defaultNamespace = defaultNamespace;
    }

    public Map.Entry<String, String> ensureNamespace(String name) {
        val i = name.indexOf('.');
        return i < 0
            ? CollectUtils.ofEntry(defaultNamespace, name)
            : CollectUtils.ofEntry(name.substring(0, i), name.substring(i + 1));
    }

    /**
     * @return {@code name} if namespace is the same as default, {@code namespace + '.' + name} otherwise
     * @see #defaultNamespace
     */
    public String stripNamespace(String namespace, String name) {
        return this.defaultNamespace.equals(namespace) ? name : namespace + '.' + name;
    }

    public void readFromFile() {
        if (!Files.exists(path)) {
            return;
        }
        try (val reader = Files.newBufferedReader(path)) {
            val object = ProbeJS.GSON.fromJson(reader, JsonObject.class);
            serde.fromJson(object);
        } catch (Exception e) {
            ProbeJS.LOGGER.error("Error happened when reading configs from file", e);
        }
    }

    public void save() {
        try (val writer = Files.newBufferedWriter(path)) {
            ProbeJS.GSON_WRITER.toJson(serde.toJson(), writer);
        } catch (Exception e) {
            ProbeJS.LOGGER.error("Error happened when writing configs to file", e);
        }
    }

    public ConfigEntry<?> get(String name) {
        return get(defaultNamespace, name);
    }

    public ConfigEntry<?> get(String namespace, String name) {
        return all.get(namespace, name);
    }

    public ConfigEntryBuilder<Void> define(String name) {
        return define(defaultNamespace, name);
    }

    public ConfigEntryBuilder<Void> define(String namespace, String name) {
        return new ConfigEntryBuilder<>(this, namespace, name);
    }

    public <T> ConfigEntry<T> register(ConfigEntry<T> entry) {
        Asser.tNotNull(entry, "config entry");
        Asser.t(
            all.get(entry.namespace, entry.name) == null,
            "a config entry with same namespace and name already exists"
        );
        Asser.t(
            entry.source == this,
            "config source in config entry not matching config source that accepts this entry"
        );
        all.put(entry.namespace, entry.name, entry);
        return entry;
    }

    public <T> ConfigEntry<T> merge(ConfigEntry<T> entry) {
        Asser.tNotNull(entry, "config entry to be merged");
        val old = all.get(entry.namespace, entry.name);
        if (old != null && old.getDefault().getClass().isInstance(entry.getDefault())) {
            old.setNoSave(Cast.to(entry.get()));
            return Cast.to(old);
        } else {
            all.put(entry.namespace, entry.name, entry);
            return entry;
        }
    }

    public Collection<ConfigEntry<?>> entries() {
        return Collections.unmodifiableCollection(all.values());
    }
}
