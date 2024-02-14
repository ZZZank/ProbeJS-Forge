package com.probejs.compiler;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.probejs.ProbeJS;
import com.probejs.ProbePaths;
import com.probejs.formatter.formatter.FormatterClass;
import com.probejs.info.EventInfo;
import com.probejs.info.type.TypeInfoClass;
import com.probejs.plugin.CapturedClasses;
import dev.latvian.kubejs.event.EventJS;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class EventCompiler {

    public static final String EVENT_CACHE_FILENAME = "cachedEvents.json";
    public static final String FORGE_EVENT_CACHE_FILENAME = "cachedForgeEvents.json";
    public static final Path EVENT_CACHE_PATH = ProbePaths.CACHE.resolve(EVENT_CACHE_FILENAME);
    public static final Path FORGE_EVENT_CACHE_PATH = ProbePaths.CACHE.resolve(FORGE_EVENT_CACHE_FILENAME);

    public static void compileEvents(
        Map<String, EventInfo> cachedEvents,
        Map<String, Class<?>> cachedForgeEvents
    ) throws IOException {
        cachedEvents.putAll(CapturedClasses.capturedEvents);
        cachedForgeEvents.putAll(CapturedClasses.capturedRawEvents);
        BufferedWriter writer = Files.newBufferedWriter(ProbePaths.GENERATED.resolve("events.d.ts"));
        writer.write("/// <reference path=\"./globals.d.ts\" />\n");
        // writer.write("/// <reference path=\"./registries.d.ts\" />\n");
        Map<String, EventInfo> wildcards = new HashMap<>();
        writeEvents(cachedEvents, writer, wildcards);
        writeWildcardEvents(writer, wildcards);
        writeForgeEvents(cachedForgeEvents, writer);
        // RegistryCompiler.compileEventRegistries(writer);
        writer.flush();
        writer.close();
    }

    private static void writeForgeEvents(Map<String, Class<?>> cachedForgeEvents, BufferedWriter writer)
        throws IOException {
        for (Map.Entry<String, Class<?>> entry : (new TreeMap<>(cachedForgeEvents)).entrySet()) {
            String name = entry.getKey();
            Class<?> event = entry.getValue();
            writer.write(
                String.format(
                    "declare function onForgeEvent(name: \"%s\", handler: (event: %s) => void);\n",
                    name,
                    FormatterClass.formatTypeParameterized(new TypeInfoClass(event))
                )
            );
        }
    }

    private static void writeWildcardEvents(BufferedWriter writer, Map<String, EventInfo> wildcards)
        throws IOException {
        for (EventInfo wildcard : (new TreeMap<>(wildcards)).values()) {
            String id = wildcard.id;
            writer.write(
                String.format(
                    "declare function onEvent(name: `%s.${string}`, handler: (event: %s) => void);\n",
                    id,
                    FormatterClass.formatTypeParameterized(new TypeInfoClass(wildcard.captured))
                )
            );
        }
    }

    private static void writeEvents(
        Map<String, EventInfo> cachedEvents,
        BufferedWriter writer,
        Map<String, EventInfo> wildcards
    ) throws IOException {
        for (Map.Entry<String, EventInfo> entry : (new TreeMap<>(cachedEvents)).entrySet()) {
            EventInfo captured = entry.getValue();
            String id = captured.id;
            Class<?> event = captured.captured;
            if (captured.hasSub()) {
                wildcards.put(id, captured);
                id = id + "." + captured.sub;
            }
            String canCancel = captured.cancellable ? "Yes" : "No";
            List<String> typeNames = captured.scriptTypes
                .stream()
                .map(type -> type.name)
                .collect(Collectors.toList());
            if (typeNames.isEmpty()) {
                canCancel = "unknown";
                typeNames.add("unknown, info of this event seems fetched from an older version of cache");
            }
            writer.write("/**" + "\n");
            writer.write(" * @cancellable " + canCancel + "\n");
            writer.write(" * @at " + String.join(", ", typeNames) + "\n");
            writer.write(" */" + "\n");
            writer.write(
                String.format(
                    "declare function onEvent(name: \"%s\", handler: (event: %s) => void);\n",
                    id,
                    FormatterClass.formatTypeParameterized(new TypeInfoClass(event))
                )
            );
        }
    }

    public static Map<String, EventInfo> readCachedEvents() throws IOException {
        Map<String, EventInfo> cachedEvents = new HashMap<>();
        if (!Files.exists(EVENT_CACHE_PATH)) {
            return cachedEvents;
        }
        try {
            JsonObject cachedMap = ProbeJS.GSON.fromJson(
                Files.newBufferedReader(EVENT_CACHE_PATH),
                JsonObject.class
            );
            for (Map.Entry<String, JsonElement> entry : cachedMap.entrySet()) {
                String key = entry.getKey();
                JsonElement value = entry.getValue();
                if (!value.isJsonObject()) {
                    //old cache is string, which means JsonElement, so not JsonObject
                    break;
                }
                EventInfo.fromJson(value.getAsJsonObject()).ifPresent(event -> cachedEvents.put(key, event));
            }
        } catch (JsonSyntaxException | JsonIOException e) {
            ProbeJS.LOGGER.warn("Cannot read malformed cache, ignoring.");
        }
        return cachedEvents;
    }

    public static void writeEvents2Cache(Map<String, EventInfo> events) throws IOException {
        BufferedWriter cacheWriter = Files.newBufferedWriter(EVENT_CACHE_PATH);
        JsonObject outJson = new JsonObject();
        for (Map.Entry<String, EventInfo> entry : events.entrySet()) {
            String eventName = entry.getKey();
            EventInfo eventClass = entry.getValue();
            outJson.add(eventName, eventClass.toJson());
        }
        ProbeJS.GSON.toJson(outJson, cacheWriter);
        cacheWriter.flush();
    }

    public static Map<String, Class<?>> readCachedForgeEvents() throws IOException {
        Map<String, Class<?>> cachedEvents = new HashMap<>();
        if (!Files.exists(FORGE_EVENT_CACHE_PATH)) {
            ProbeJS.LOGGER.warn("No event cache file: {}", FORGE_EVENT_CACHE_FILENAME);
            return cachedEvents;
        }
        try {
            Map<?, ?> fileCache = ProbeJS.GSON.fromJson(
                Files.newBufferedReader(FORGE_EVENT_CACHE_PATH),
                Map.class
            );
            fileCache.forEach((k, v) -> {
                if (!(k instanceof String) || !(v instanceof String)) {
                    ProbeJS.LOGGER.warn("Unexpected entry in class cache: {}, {}", k, v);
                    return;
                }
                try {
                    Class<?> clazz = Class.forName((String) v);
                    if (EventJS.class.isAssignableFrom(clazz)) {
                        cachedEvents.put((String) k, clazz);
                    }
                } catch (ClassNotFoundException e) {
                    ProbeJS.LOGGER.warn("Class {} was in the cache, but disappeared in packages now.", v);
                }
            });
        } catch (JsonSyntaxException | JsonIOException e) {
            ProbeJS.LOGGER.warn("Cannot read malformed cache, ignoring.");
        }
        return cachedEvents;
    }

    public static void writeForgeEvents2Cache(Map<String, Class<?>> events) throws IOException {
        BufferedWriter cacheWriter = Files.newBufferedWriter(FORGE_EVENT_CACHE_PATH);
        JsonObject outJson = new JsonObject();
        for (Map.Entry<String, Class<?>> entry : events.entrySet()) {
            String eventName = entry.getKey();
            Class<?> eventClass = entry.getValue();
            outJson.addProperty(eventName, eventClass.getName());
        }
        ProbeJS.GSON.toJson(outJson, cacheWriter);
        cacheWriter.flush();
    }
}
