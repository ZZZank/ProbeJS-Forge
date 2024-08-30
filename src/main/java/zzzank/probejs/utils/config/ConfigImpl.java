package zzzank.probejs.utils.config;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.util.UtilsJS;
import lombok.val;
import zzzank.probejs.ProbeJS;

import java.nio.file.Files;
import java.nio.file.Path;
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

    public void readFromFile() {
        try (val reader = Files.newBufferedReader(path)) {
            val object = ProbeJS.GSON.fromJson(reader, JsonObject.class);
            JsonConfigParser.select(object).parse(this, object);
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
                .forEach(pair -> object.add(pair.getFirst(), pair.getSecond()));
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

    public <T> ConfigEntry<T> addConfig(ConfigEntryBuilder<T> builder) {
        val configEntry = builder.build(this);
        all.put(configEntry.namespace, configEntry.name, configEntry);
        return configEntry;
    }

    public ConfigEntry<?> merge(ConfigEntry<?> configEntry) {
        Objects.requireNonNull(configEntry);
        val old = all.get(configEntry.namespace, configEntry.name);
        if (old != null) {
            old.set(UtilsJS.cast(old.adaptValue(configEntry.get())));
            return old;
        } else {
            all.put(configEntry.namespace, configEntry.name, configEntry);
            return configEntry;
        }
    }
}
