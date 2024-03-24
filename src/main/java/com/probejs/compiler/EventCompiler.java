package com.probejs.compiler;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.probejs.ProbeJS;
import com.probejs.ProbePaths;
import com.probejs.formatter.formatter.FormatterType;
import com.probejs.info.EventInfo;
import com.probejs.info.type.TypeInfoClass;
import dev.latvian.kubejs.event.EventJS;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class EventCompiler {

    private static final String EVENT_CACHE_FILENAME = "cachedEvents.json";
    private static final String FORGE_EVENT_CACHE_FILENAME = "cachedForgeEvents.json";
    private static final Path EVENT_CACHE_PATH = ProbePaths.CACHE.resolve(EVENT_CACHE_FILENAME);
    private static final Path FORGE_EVENT_CACHE_PATH = ProbePaths.CACHE.resolve(FORGE_EVENT_CACHE_FILENAME);

    private static Map<String, EventInfo> cachedEvents;
    private static Map<String, EventInfo> wildcards;
    private static Map<String, Class<?>> cachedForgeEvents;

    public static void compileEvents(
        Map<String, EventInfo> cachedEvents,
        Map<String, Class<?>> cachedForgeEvents
    ) throws IOException {
        EventCompiler.cachedEvents = cachedEvents;
        EventCompiler.cachedForgeEvents = cachedForgeEvents;
        EventCompiler.wildcards = new HashMap<>();
        BufferedWriter writer = Files.newBufferedWriter(ProbePaths.GENERATED.resolve("events.d.ts"));

        writer.write("/// <reference path=\"./globals.d.ts\" />\n");
        // writer.write("/// <reference path=\"./special.d.ts\" />\n");
        writeEvents(writer);
        writeWildcardEvents(writer);
        writeForgeEvents(writer);
        // RegistryCompiler.compileEventRegistries(writer);
        writer.flush();

        EventCompiler.cachedEvents = null;
        EventCompiler.cachedForgeEvents = null;
        EventCompiler.wildcards = null;
        writer.close();
    }

    private static void writeForgeEvents(BufferedWriter writer) throws IOException {
        final List<String> lines = new ArrayList<>();
        for (Map.Entry<String, Class<?>> entry : (new TreeMap<>(cachedForgeEvents)).entrySet()) {
            String name = entry.getKey();
            Class<?> event = entry.getValue();
            lines.add(
                String.format(
                    "declare function onForgeEvent(name: \"%s\", handler: (event: %s) => void);\n",
                    name,
                    FormatterType.formatParameterized(new TypeInfoClass(event))
                )
            );
        }
        lines.add("\n");
        for (final String line : lines) {
            writer.write(line);
            writer.write("\n");
        }
    }

    private static void writeWildcardEvents(BufferedWriter writer) throws IOException {
        final List<String> lines = new ArrayList<>();
        for (EventInfo wildcard : (new TreeMap<>(wildcards)).values()) {
            String id = wildcard.id;
            lines.addAll(wildcard.getBuiltinPropAsComment());
            lines.add(
                String.format(
                    "declare function onEvent(name: `%s.${string}`, handler: (event: %s) => void);",
                    id,
                    FormatterType.formatParameterized(new TypeInfoClass(wildcard.clazzRaw))
                )
            );
        }
        lines.add("/**");
        lines.add(
            " * This is the general representation of wildcarded event, you should replace `${string}` with actual id."
        );
        lines.add(" * ");
        lines.add(" * E.g. `player.data_from_server.reload`, `ftbquests.completed.123456`");
        lines.add(" */");
        lines.add(
            "declare function onEvent(name: `${string}.${string}`, handler: (event: Internal.EventJS) => void);"
        );
        lines.add("");
        for (final String line : lines) {
            writer.write(line);
            writer.write("\n");
        }
    }

    private static void writeEvents(BufferedWriter writer) throws IOException {
        final List<String> lines = new ArrayList<>();
        for (Map.Entry<String, EventInfo> entry : (new TreeMap<>(cachedEvents)).entrySet()) {
            EventInfo captured = entry.getValue();
            String id = captured.id;
            Class<?> event = captured.clazzRaw;
            if (captured.hasSub()) {
                wildcards.put(id, captured);
                id = id + "." + captured.sub;
            }
            lines.addAll(captured.getBuiltinPropAsComment());
            lines.add(
                String.format(
                    "declare function onEvent(name: \"%s\", handler: (event: %s) => void);",
                    id,
                    FormatterType.formatParameterized(new TypeInfoClass(event))
                )
            );
        }
        lines.add("/**");
        lines.add(
            " * General representation of `onEvent()`, seeing this comment usually indicates that such event does not exist, or is unknown to ProbeJS yet"
        );
        lines.add(" */");
        lines.add("declare function onEvent(name: string, handler: (event: Internal.EventJS) => void);");
        lines.add("");
        for (final String line : lines) {
            writer.write(line);
            writer.write("\n");
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

    public static void compileEventsCache(Map<String, EventInfo> events) throws IOException {
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

    public static void comileForgeEventsCache(Map<String, Class<?>> events) throws IOException {
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
