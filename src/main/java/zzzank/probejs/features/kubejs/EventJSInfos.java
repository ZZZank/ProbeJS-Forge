package zzzank.probejs.features.kubejs;

import com.google.gson.JsonObject;
import lombok.val;
import zzzank.probejs.ProbeJS;

import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * @author ZZZank
 */
public final class EventJSInfos {

    public static final Map<String, EventJSInfo> KNOWN = new HashMap<>();

    public static Set<EventJSInfo> sortedInfos() {
        return new TreeSet<>(KNOWN.values());
    }

    public static Set<Class<?>> provideClasses() {
        return KNOWN.values().stream().map(EventJSInfo::clazzRaw).collect(Collectors.toSet());
    }

    public static void loadFrom(Path path) {
        if (!path.toFile().exists()) {
            return;
        }
        try {
            val reader = new FileReader(path.toFile());
            val obj = ProbeJS.GSON.fromJson(reader, JsonObject.class);
            for (val entry : obj.entrySet()) {
                val id = entry.getKey();
                val info = EventJSInfo.fromJson(entry.getValue().getAsJsonObject());
                if (info != null) {
                    KNOWN.put(id, info);
                }
            }
        } catch (Exception e) {
            ProbeJS.LOGGER.error("Cannot read EventJS infos", e);
        }
    }

    public static void writeTo(Path path) {
        try {
            val obj = new JsonObject();
            for (val info : KNOWN.values()) {
                obj.add(info.id(), info.toJson());
            }
            val writer = new FileWriter(path.toFile());
            ProbeJS.GSON_WRITER.toJson(obj, writer);
        } catch (Exception e) {
            ProbeJS.LOGGER.error("Cannot write EventJS infos", e);
        }
    }
}
