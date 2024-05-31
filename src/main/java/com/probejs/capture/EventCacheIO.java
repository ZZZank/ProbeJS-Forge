package com.probejs.capture;

import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.probejs.ProbeJS;
import com.probejs.compiler.EventCompiler;
import com.probejs.info.EventInfo;
import com.probejs.util.Pair;
import com.probejs.util.json.JObject;
import dev.latvian.kubejs.event.EventJS;
import lombok.val;

import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public abstract class EventCacheIO {
    public static Map<String, EventInfo> readKjs() throws IOException {
        val cachedEvents = new HashMap<String, EventInfo>();
        if (!Files.exists(EventCompiler.EVENT_CACHE_PATH)) {
            return cachedEvents;
        }
        try {
            val cachedMap = ProbeJS.GSON.fromJson(
                Files.newBufferedReader(EventCompiler.EVENT_CACHE_PATH),
                JsonObject.class
            );
            if (cachedMap == null) {
                return cachedEvents;
            }
            for (val entry : cachedMap.entrySet()) {
                val key = entry.getKey();
                val value = entry.getValue();
                if (!value.isJsonObject()) {
                    //old cache is string, which means JsonElement, so not JsonObject
                    break;
                }
                EventInfo.fromJson(value.getAsJsonObject()).ifPresent(event -> cachedEvents.put(key, event));
            }
        } catch (JsonSyntaxException | JsonIOException e) {
            ProbeJS.LOGGER.warn("Cannot read malformed KubeJS event cache, ignoring.");
        }
        return cachedEvents;
    }

    public static void writeKjs(Map<String, EventInfo> events) throws IOException {
        val cacheWriter = Files.newBufferedWriter(EventCompiler.EVENT_CACHE_PATH);
        val outJson = JObject.of()
            .addAll(events.entrySet()
                .stream()
                .map(entry -> new Pair<>(entry.getKey(), JObject.of(entry.getValue().toJson())))
            )
            .build();
        ProbeJS.GSON.toJson(outJson, cacheWriter);
        cacheWriter.close();
    }

    public static Map<String, Class<?>> readForge() throws IOException {
        val cachedEvents = new HashMap<String, Class<?>>();
        if (!Files.exists(EventCompiler.FORGE_EVENT_CACHE_PATH)) {
            ProbeJS.LOGGER.warn("No event cache file: {}", EventCompiler.FORGE_EVENT_CACHE_NAME);
            return cachedEvents;
        }
        try {
            final Map<?, ?> fileCache = ProbeJS.GSON.fromJson(
                Files.newBufferedReader(EventCompiler.FORGE_EVENT_CACHE_PATH),
                Map.class
            );
            if (fileCache == null) {
                return cachedEvents;
            }
            fileCache.forEach((k, v) -> {
                if (!(k instanceof String) || !(v instanceof String)) {
                    ProbeJS.LOGGER.warn("Unexpected entry in class cache: {}, {}", k, v);
                    return;
                }
                try {
                    val clazz = Class.forName((String) v);
                    if (EventJS.class.isAssignableFrom(clazz)) {
                        cachedEvents.put((String) k, clazz);
                    }
                } catch (ClassNotFoundException e) {
                    ProbeJS.LOGGER.warn("Class {} was in the cache, but disappeared in packages now.", v);
                }
            });
        } catch (JsonSyntaxException | JsonIOException e) {
            ProbeJS.LOGGER.warn("Cannot read malformed Forge event cache, ignoring.");
        }
        return cachedEvents;
    }

    public static void writeForge(Map<String, Class<?>> events) throws IOException {
        val cacheWriter = Files.newBufferedWriter(EventCompiler.FORGE_EVENT_CACHE_PATH);
        val outJson = new JsonObject();
        for (Map.Entry<String, Class<?>> entry : events.entrySet()) {
            val eventName = entry.getKey();
            val eventClass = entry.getValue();
            outJson.addProperty(eventName, eventClass.getName());
        }
        ProbeJS.GSON.toJson(outJson, cacheWriter);
        cacheWriter.close();
    }
}
