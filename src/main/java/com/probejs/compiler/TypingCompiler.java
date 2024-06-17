package com.probejs.compiler;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonObject;
import com.probejs.ProbeJS;
import com.probejs.ProbePaths;
import com.probejs.capture.EventCacheIO;
import com.probejs.document.DocManager;
import com.probejs.formatter.resolver.ClazzFilter;
import com.probejs.formatter.resolver.PathResolver;
import com.probejs.formatter.resolver.SpecialTypes;
import com.probejs.formatter.FormatterClass;
import com.probejs.formatter.FormatterNamespace;
import com.probejs.formatter.FormatterRaw;
import com.probejs.formatter.api.IFormatter;
import com.probejs.info.EventInfo;
import com.probejs.info.clazz.ClassInfo;
import com.probejs.info.SpecialData;
import com.probejs.info.ClassWalker;
import com.probejs.info.type.JavaTypeClass;
import com.probejs.capture.CapturedClasses;
import com.probejs.capture.DummyBindingEvent;
import com.probejs.util.PUtil;
import com.probejs.util.json.JArray;
import com.probejs.util.json.JObject;
import com.probejs.util.json.JPrimitive;
import dev.latvian.kubejs.KubeJSPaths;
import dev.latvian.kubejs.recipe.RecipeTypeJS;
import dev.latvian.kubejs.server.ServerScriptManager;

import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.val;
import net.minecraft.resources.ResourceLocation;

public class TypingCompiler {

    public static Set<Class<?>> fetchClasses(
        Map<ResourceLocation, RecipeTypeJS> recipeTypeMap,
        DummyBindingEvent bindingEvent,
        Set<Class<?>> cachedClasses
    ) {
        // cache
        Set<Class<?>> touchableClasses = new HashSet<>(cachedClasses);
        //captured
        CapturedClasses.capturedEvents
            .values()
            .stream()
            .map(EventInfo::clazzRaw)
            .forEach(touchableClasses::add);
        touchableClasses.addAll(CapturedClasses.capturedRawEvents.values());
        touchableClasses.addAll(CapturedClasses.capturedJavaClasses);
        //recipe type
        recipeTypeMap
            .values()
            .stream()
            .map(recipeTypeJS -> recipeTypeJS.factory.get().getClass())
            .forEach(touchableClasses::add);
        //binding event
        touchableClasses.addAll(bindingEvent.getTouchedConstantDump());
        touchableClasses.addAll(bindingEvent.getClassDumpMap().values());

        ClassWalker walker = new ClassWalker(touchableClasses);
        return walker.walk();
    }

    public static void compileGlobal(Set<Class<?>> globalClasses) throws IOException {
        val writer = Files.newBufferedWriter(ProbePaths.GENERATED.resolve("globals.d.ts"));
        writer.write("/// <reference path=\"./special.d.ts\" />\n");
        Multimap<String, IFormatter> namespaced = ArrayListMultimap.create();

        for (Class<?> clazz : globalClasses) {
            val formatter = new FormatterClass(ClassInfo.ofCache(clazz));
            DocManager.classDocuments.get(clazz.getName()).forEach(formatter::addDocument);

            val name = PathResolver.getResolvedName(clazz.getName());
            if (name.namespace().isEmpty()) {
                for (String line : formatter.formatLines(0, 4)) {
                    writer.write(line);
                    writer.write("\n");
                }
                if (clazz.isInterface()) {
                    String fullName = name.fullPath();
                    writer.write(String.format("declare const %s: %s;\n", fullName, fullName));
                }
            } else {
                formatter.setInternal(true);
                namespaced.put(name.namespace(), formatter);
            }
        }

        for (val entry : namespaced.asMap().entrySet()) {
            val path = entry.getKey();
            val formatters = entry.getValue();
            val namespace = new FormatterNamespace(path, formatters);
            PUtil.writeLines(writer, namespace.formatLines(0, 4));
        }

        for (val entry : DocManager.classAdditions.entrySet()) {
            val document = entry.getValue();
            val baseDoc = document.get(0);
            document.subList(1, document.size()).forEach(baseDoc::merge);
        }

        //namespace::Document
        PUtil.writeLines(writer, new FormatterNamespace(
            "Document",
            DocManager.classAdditions.values().stream().map(l -> l.get(0)).collect(Collectors.toList())
        ).formatLines(0, 4));
        //namespace::Type
        PUtil.writeLines(writer, new FormatterNamespace("Type", DocManager.typeDocuments).formatLines(0, 4));
        //no namespace
        PUtil.writeLines(writer, new FormatterRaw(DocManager.rawTSDoc).formatLines(0, 4));

        writer.close();
    }

    public static void compileConstants(DummyBindingEvent bindingEvent) throws IOException {
        val writer = Files.newBufferedWriter(ProbePaths.GENERATED.resolve("constants.d.ts"));
        writer.write("/// <reference path=\"./globals.d.ts\" />\n");
        for (val entry : (new TreeMap<>(bindingEvent.getConstantDumpMap())).entrySet()) {
            val name = entry.getKey();
            val value = entry.getValue();
            String resolved = PathResolver.formatValue(value);
            if (resolved == null) {
                resolved = SpecialTypes.forceParameterizedFormat(new JavaTypeClass(value.getClass()));
            }
            writer.write(String.format("declare const %s: %s;\n", name, resolved));
        }
        writer.close();
    }

    public static void compileJava(Set<Class<?>> globalClasses) throws IOException {
        val writer = Files.newBufferedWriter(ProbePaths.GENERATED.resolve("java.d.ts"));
        val lines = new ArrayList<String>(100);
        lines.add("/// <reference path=\"./globals.d.ts\" />");
        lines.add("");
        globalClasses
            .stream()
            .filter(c -> ServerScriptManager.instance.scriptManager.isClassAllowed(c.getName()))
            .sorted(Comparator.comparing(Class::getName))
            .map(c ->
                String.format(
                    "declare function java(name: %s): typeof %s;",
                    ProbeJS.GSON.toJson(c.getName()),
                    SpecialTypes.forceParameterizedFormat(new JavaTypeClass(c))
                )
            )
            .forEach(lines::add);
        for (String line : lines) {
            writer.write(line);
            writer.write('\n');
        }
        writer.close();
    }

    public static void compileJSConfig() throws IOException {
        val path = KubeJSPaths.DIRECTORY.resolve("jsconfig.json");
        val cfg = JObject.of()
            .add("compilerOptions",
                JObject.of()
                    .add("lib", JArray.of().add("ES6"))
                    .add("rootDirs",
                        JArray.of()
                            .addAll(Stream.of("probe/generated",
                                    "probe/user",
                                    "server_scripts",
                                    "startup_scripts",
                                    "client_scripts"
                                )
                                .map(JPrimitive::of))
                    )
            )
            .build();
        if (Files.exists(path)) {
            try (val reader = Files.newBufferedReader(path)) {
                val existed = ProbeJS.GSON.fromJson(reader, JsonObject.class);
                PUtil.mergeJsonRecursive(cfg, existed);
            } catch (Exception ignored) {
            }
        }
        val writer = Files.newBufferedWriter(path);

        ProbeJS.GSON.toJson(cfg, writer);
        writer.close();
    }

    public static void compile() throws IOException {
        val bindingEvent = SpecialData.computeBindingEvent();
        val typeMap = SpecialData.computeRecipeTypes();
        val knownEvents = EventCacheIO.readKjs();
        knownEvents.putAll(CapturedClasses.capturedEvents);
        val knownRawEvents = EventCacheIO.readForge();
        knownRawEvents.putAll(CapturedClasses.capturedRawEvents);

        final Set<Class<?>> cachedClasses = knownEvents
            .values()
            .stream()
            .map(EventInfo::clazzRaw)
            .collect(Collectors.toSet());
        cachedClasses.addAll(knownRawEvents.values());

        //global class
        val globalClasses = fetchClasses(typeMap, bindingEvent, cachedClasses);
        globalClasses.removeIf(ClazzFilter::shouldSkip);

        bindingEvent.getClassDumpReversed().forEach((c, names) -> {
            val base = names.get(0);
            PathResolver.resolveManually(c, base);
            for (int i = 1; i < names.size(); i++) {
                DocManager.rawTSDoc.add(String.format("declare const %s = %s", names.get(i), base));
            }
        });
        PathResolver.resolveNames(globalClasses);

        SpecialTypes.processSpecialAssignments();

        compileGlobal(globalClasses);
        SpecialCompiler.compile(typeMap);
        EventCompiler.compile(knownEvents, knownRawEvents);
        compileConstants(bindingEvent);
        compileJava(globalClasses);
        compileJSConfig();
        EventCacheIO.writeKjs(knownEvents);
        EventCacheIO.writeForge(knownRawEvents);
    }
}
