package com.probejs.compiler;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.probejs.ProbeJS;
import com.probejs.ProbePaths;
import com.probejs.formatter.formatter.FormatterComments;
import com.probejs.formatter.formatter.FormatterType;
import com.probejs.info.EventInfo;
import com.probejs.info.type.TypeInfoClass;
import dev.latvian.kubejs.event.EventJS;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class EventCompiler {

    public static final String EVENT_CACHE_NAME = "cachedEvents.json";
    public static final String FORGE_EVENT_CACHE_NAME = "cachedForgeEvents.json";
    public static final Path EVENT_CACHE_PATH = ProbePaths.CACHE.resolve(EVENT_CACHE_NAME);
    public static final Path FORGE_EVENT_CACHE_PATH = ProbePaths.CACHE.resolve(FORGE_EVENT_CACHE_NAME);

    private static Collection<EventInfo> knownEvents;
    private static Set<EventInfo> wildcards;
    private static Collection<Class<?>> knownForgeEvents;

    public static void compile(Map<String, EventInfo> knownEvents, Map<String, Class<?>> knownForgeEvents)
        throws IOException {
        EventCompiler.knownEvents = knownEvents.values();
        EventCompiler.knownForgeEvents = knownForgeEvents.values();
        EventCompiler.wildcards =
            knownEvents.values().stream().filter(EventInfo::hasSub).collect(Collectors.toSet());
        BufferedWriter writer = Files.newBufferedWriter(ProbePaths.GENERATED.resolve("events.d.ts"));

        writer.write("/// <reference path=\"./globals.d.ts\" />\n");
        writeEvents(writer);
        writeWildcardEvents(writer);
        writeForgeEvents(writer);

        EventCompiler.knownEvents = null;
        EventCompiler.knownForgeEvents = null;
        EventCompiler.wildcards = null;
        writer.close();
    }

    private static void writeForgeEvents(BufferedWriter writer) throws IOException {
        final List<String> lines = new ArrayList<>();
        knownForgeEvents
            .stream()
            .sorted(Comparator.comparing(Class::getName))
            .map(clazz ->
                String.format(
                    "declare function onForgeEvent(name: %s, handler: (event: %s) => void);",
                    ProbeJS.GSON.toJson(clazz.getName()),
                    FormatterType.formatParameterized(new TypeInfoClass(clazz))
                )
            )
            .forEach(lines::add);
        lines.add("");
        for (final String line : lines) {
            writer.write(line);
            writer.write("\n");
        }
    }

    private static void writeWildcardEvents(BufferedWriter writer) throws IOException {
        final List<String> lines = new ArrayList<>();
        for (EventInfo wildcard : (new TreeSet<>(wildcards))) {
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
        lines.addAll(
            new FormatterComments(
                "This is the general representation of wildcarded event, you should replace `${string}` with actual id.",
                "",
                "E.g. `player.data_from_server.reload`, `ftbquests.completed.123456`"
            )
                .format(0, 0)
        );
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
        for (EventInfo eInfo : (new TreeSet<>(knownEvents))) {
            String id = eInfo.id;
            if (eInfo.hasSub()) {
                id = id + "." + eInfo.sub;
            }
            lines.addAll(eInfo.getBuiltinPropAsComment());
            lines.add(
                String.format(
                    "declare function onEvent(name: \"%s\", handler: (event: %s) => void);",
                    id,
                    FormatterType.formatParameterized(new TypeInfoClass(eInfo.clazzRaw))
                )
            );
        }
        lines.addAll(
            Arrays.asList(
                "/**",
                " * this is the general representation of `onEvent()`, seeing this comment usually indicates that such event does not exist,",
                " * or is unknown to ProbeJS yet",
                " */",
                "declare function onEvent(name: string, handler: (event: Internal.EventJS) => void);",
                ""
            )
        );
        for (final String line : lines) {
            writer.write(line);
            writer.write("\n");
        }
    }

    public static Map<String, EventInfo> readCachedEvents() throws IOException {
        final Map<String, EventInfo> cachedEvents = new HashMap<>();
        if (!Files.exists(EVENT_CACHE_PATH)) {
            return cachedEvents;
        }
        try {
            final JsonObject cachedMap = ProbeJS.GSON.fromJson(
                Files.newBufferedReader(EVENT_CACHE_PATH),
                JsonObject.class
            );
            for (Map.Entry<String, JsonElement> entry : cachedMap.entrySet()) {
                final String key = entry.getKey();
                final JsonElement value = entry.getValue();
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
        final BufferedWriter cacheWriter = Files.newBufferedWriter(EVENT_CACHE_PATH);
        final JsonObject outJson = new JsonObject();
        for (final Map.Entry<String, EventInfo> entry : events.entrySet()) {
            final String eventName = entry.getKey();
            final EventInfo eventClass = entry.getValue();
            outJson.add(eventName, eventClass.toJson());
        }
        ProbeJS.GSON.toJson(outJson, cacheWriter);
        cacheWriter.close();
    }

    public static Map<String, Class<?>> readCachedForgeEvents() throws IOException {
        final Map<String, Class<?>> cachedEvents = new HashMap<>();
        if (!Files.exists(FORGE_EVENT_CACHE_PATH)) {
            ProbeJS.LOGGER.warn("No event cache file: {}", FORGE_EVENT_CACHE_NAME);
            return cachedEvents;
        }
        try {
            final Map<?, ?> fileCache = ProbeJS.GSON.fromJson(
                Files.newBufferedReader(FORGE_EVENT_CACHE_PATH),
                Map.class
            );
            fileCache.forEach((k, v) -> {
                if (!(k instanceof String) || !(v instanceof String)) {
                    ProbeJS.LOGGER.warn("Unexpected entry in class cache: {}, {}", k, v);
                    return;
                }
                try {
                    final Class<?> clazz = Class.forName((String) v);
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

    public static void compileForgeEventsCache(Map<String, Class<?>> events) throws IOException {
        final BufferedWriter cacheWriter = Files.newBufferedWriter(FORGE_EVENT_CACHE_PATH);
        final JsonObject outJson = new JsonObject();
        for (Map.Entry<String, Class<?>> entry : events.entrySet()) {
            final String eventName = entry.getKey();
            final Class<?> eventClass = entry.getValue();
            outJson.addProperty(eventName, eventClass.getName());
        }
        ProbeJS.GSON.toJson(outJson, cacheWriter);
        cacheWriter.close();
    }
}
