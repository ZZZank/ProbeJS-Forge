package zzzank.probejs.utils.config;

import com.google.gson.JsonObject;
import lombok.val;
import zzzank.probejs.ProbeConfig;
import zzzank.probejs.ProbeJS;
import zzzank.probejs.utils.CollectUtils;
import zzzank.probejs.utils.JsonUtils;

import java.util.List;

/**
 * @author ZZZank
 */
public interface JsonConfigParser {

    List<JsonConfigParser> REGISTERED = CollectUtils.ofList(
        new ParserOld(),
        new ParserVersion2(),
        new ParserVersion3()
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

    class ParserOld implements JsonConfigParser {

        @Override
        public boolean test(JsonObject rawConfig) {
            return !rawConfig.has(ConfigImpl.VERSION_KEY);
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

    class ParserVersion2 implements JsonConfigParser {

        @Override
        public boolean test(JsonObject rawConfig) {
            return rawConfig.has(ConfigImpl.VERSION_KEY);
        }

        @Override
        public void parse(ConfigImpl source, JsonObject rawConfig) {
            rawConfig.remove(ConfigImpl.VERSION_KEY);
            for (val entry : rawConfig.entrySet()) {
                val configEntry = source.serde.fromJson(entry.getKey(), entry.getValue().getAsJsonObject());
                if (configEntry == null) {
                    continue;
                }
                source.merge(configEntry);
            }
        }
    }

    class ParserVersion3 implements JsonConfigParser {

        @Override
        public boolean test(JsonObject rawConfig) {
            val cfg = rawConfig.get(ProbeJS.MOD_ID + '.' + ProbeConfig.configVersion.name);
            return cfg != null && cfg.getAsJsonObject().get(ConfigEntrySerde.VALUE_KEY).getAsInt() == 3;
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
