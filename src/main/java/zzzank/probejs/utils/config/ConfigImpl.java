package zzzank.probejs.utils.config;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.util.UtilsJS;
import lombok.val;
import zzzank.probejs.ProbeJS;
import zzzank.probejs.utils.CollectUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;

/**
 * @author ZZZank
 */
public class ConfigImpl {

    public final String defaultNamespace;
    public final ConfigEntrySerde serde;
    private final Table<String, String, ConfigEntry<?>> all;
    public final Path path;

    public ConfigImpl(Path path, String defaultNamespace) {
        this.path = path;
        this.serde = new ConfigEntrySerde(this);
        all = HashBasedTable.create();
        this.defaultNamespace = defaultNamespace;
    }

    public Map.Entry<String, String> ensureNamespace(String name) {
        val i = name.indexOf('.');
        if (i < 0) {
            return CollectUtils.ofEntry(defaultNamespace, name);
        }
        return CollectUtils.ofEntry(name.substring(0, i), name.substring(i + 1));
    }

    public void readFromFile() {
        try (val reader = Files.newBufferedReader(path)) {
            val object = ProbeJS.GSON.fromJson(reader, JsonObject.class);
            for (val entry : object.entrySet()) {
                val e = serde.fromJson(entry.getKey(), entry.getValue().getAsJsonObject());
                if (e == null || this.get(e.namespace, e.name) == null) {
                    continue;
                }
                merge(e);
            }
        } catch (Exception e) {
            ProbeJS.LOGGER.error("Error happened when reading configs from file", e);
        }
    }

    public void save() {
        try (val writer = Files.newBufferedWriter(path)) {
            val object = new JsonObject();
            all.cellSet()
                .stream()
                .map(Table.Cell::getValue)
                .filter(Objects::nonNull)
                .map(serde::toJson)
                .forEach(pair -> object.add(pair.getKey(), pair.getValue()));
            ProbeJS.GSON_WRITER.toJson(object, writer);
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

    public ConfigEntryBuilder<Object> define(String name) {
        return new ConfigEntryBuilder<>(this, name);
    }

    public <T> ConfigEntry<T> merge(ConfigEntry<T> entry) {
        Objects.requireNonNull(entry);
        val old = all.get(entry.namespace, entry.name);
        if (old != null && old.defaultValue.getClass().isInstance(entry.defaultValue)) {
            old.setNoSave(UtilsJS.cast(entry.get()));
            return (ConfigEntry<T>) old;
        } else {
            all.put(entry.namespace, entry.name, entry);
            return entry;
        }
    }
}
