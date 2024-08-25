package zzzank.probejs.utils.config;

import com.google.gson.JsonObject;
import lombok.val;
import zzzank.probejs.utils.CollectUtils;
import zzzank.probejs.utils.JsonUtils;

import java.util.List;

/**
 * @author ZZZank
 */
public interface JsonConfigParser {

    String VERSION_KEY = "$version";
    List<JsonConfigParser> REGISTERED = CollectUtils.ofList(
        new OldParser(),
        new DefaultParser()
    );

    static JsonConfigParser select(JsonObject rawConfig) {
        for (JsonConfigParser parser : REGISTERED) {
            if (parser.test(rawConfig)) {
                return parser;
            }
        }
        throw new IllegalStateException("No JsonConfigParser available for provided rawConfig");
    }

    boolean test(JsonObject rawConfig);

    void parse(ConfigImpl source, JsonObject rawConfig);

    class OldParser implements JsonConfigParser {

        @Override
        public boolean test(JsonObject rawConfig) {
            return !rawConfig.has(VERSION_KEY);
        }

        @Override
        public void parse(ConfigImpl source, JsonObject rawConfig) {
            for (val entry : rawConfig.entrySet()) {
                val key = entry.getKey().split("\\.", 2);
                val value = JsonUtils.deserializeObject(entry.getValue());
                val configEntry = ConfigEntryBuilder.of(value).setNamespace(key[0]).setName(key[1]).build(source);
                source.merge(configEntry);
            }
        }
    }

    class DefaultParser implements JsonConfigParser {

        @Override
        public boolean test(JsonObject rawConfig) {
            return true;
        }

        @Override
        public void parse(ConfigImpl source, JsonObject rawConfig) {
            for (val entry : rawConfig.entrySet()) {
                val configEntry = source.serde.fromJson(entry.getKey(), entry.getValue().getAsJsonObject());
                if (configEntry == null) {
                    continue;
                }
                source.merge(configEntry);
            }
        }
    }
}
