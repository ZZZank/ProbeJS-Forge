package zzzank.probejs.features.kubejs;

import com.google.gson.JsonObject;
import lombok.val;
import zzzank.probejs.ProbeJS;

import java.nio.file.Files;
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
        try (val reader = Files.newBufferedReader(path)) {
            val obj = ProbeJS.GSON.fromJson(reader, JsonObject.class);
            if (obj == null) {
                return;
            }
            for (val entry : obj.entrySet()) {
                val id = entry.getKey();
                val info = EventJSInfo.fromJson(id, entry.getValue().getAsJsonObject());
                if (info != null) {
                    KNOWN.put(id, info);
                }
            }
        } catch (Exception e) {
            ProbeJS.LOGGER.error("Error when reading EventJS infos", e);
        }
    }

    public static void writeTo(Path path) {
        try (val writer = Files.newBufferedWriter(path)) {
            val obj = new JsonObject();
            for (val info : KNOWN.values()) {
                val pair = info.toJson();
                obj.add(pair.getFirst(), pair.getSecond());
            }
            //dont use ProbeJS.GSON_WRITER, there's too many lines
            ProbeJS.GSON.toJson(obj, writer);
        } catch (Exception e) {
            ProbeJS.LOGGER.error("Error when writing EventJS infos", e);
        }
    }
}
