package com.probejs.compiler;

import com.probejs.ProbeJS;
import com.probejs.ProbePaths;
import com.probejs.formatter.FormatterComments;
import com.probejs.formatter.resolver.SpecialTypes;
import com.probejs.info.EventInfo;
import com.probejs.info.type.JavaTypeClass;
import com.probejs.util.PUtil;
import lombok.val;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
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
        EventCompiler.wildcards = knownEvents.values().stream().filter(EventInfo::hasSub).collect(Collectors.toSet());
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
        val lines = new ArrayList<String>();
        knownForgeEvents
            .stream()
            .sorted(Comparator.comparing(Class::getName))
            .map(clazz ->
                String.format(
                    "declare function onForgeEvent(name: %s, handler: (event: %s) => void): void;",
                    ProbeJS.GSON.toJson(clazz.getName()),
                    SpecialTypes.forceParameterizedFormat(new JavaTypeClass(clazz))
                )
            )
            .forEach(lines::add);
        lines.add("");
        PUtil.writeLines(writer, lines);
    }

    private static void writeWildcardEvents(BufferedWriter writer) throws IOException {
        val lines = new ArrayList<String>();
        for (EventInfo wildcard : (new TreeSet<>(wildcards))) {
            val id = wildcard.id();
            lines.addAll(wildcard.getBuiltinPropAsComment());
            lines.add(
                String.format(
                    "declare function onEvent(name: `%s.${string}`, handler: (event: %s) => void): void;",
                    id,
                    SpecialTypes.forceParameterizedFormat(new JavaTypeClass(wildcard.clazzRaw()))
                )
            );
        }
        lines.addAll(
            new FormatterComments(
                "This is the general representation of wildcarded event, you should replace `${string}` with actual id.",
                "",
                "E.g. `player.data_from_server.reload`, `ftbquests.completed.123456`"
            )
                .formatLines(0, 0)
        );
        lines.add(
            "declare function onEvent(name: `${string}.${string}`, handler: (event: Internal.EventJS) => void): void;"
        );
        lines.add("");
        PUtil.writeLines(writer, lines);
    }

    private static void writeEvents(BufferedWriter writer) throws IOException {
        val lines = new ArrayList<String>();
        for (EventInfo eInfo : (new TreeSet<>(knownEvents))) {
            String id = eInfo.id();
            if (eInfo.hasSub()) {
                id = id + "." + eInfo.sub();
            }
            lines.addAll(eInfo.getBuiltinPropAsComment());
            lines.add(
                String.format(
                    "declare function onEvent(name: \"%s\", handler: (event: %s) => void): void;",
                    id,
                    SpecialTypes.forceParameterizedFormat(new JavaTypeClass(eInfo.clazzRaw()))
                )
            );
        }
        lines.addAll(
            Arrays.asList(
                "/**",
                " * this is the general representation of `onEvent()`, seeing this comment usually indicates that such event does not exist,",
                " * or is unknown to ProbeJS yet",
                " */",
                "declare function onEvent(name: string, handler: (event: Internal.EventJS) => void): void;",
                ""
            )
        );
        PUtil.writeLines(writer, lines);
    }
}
