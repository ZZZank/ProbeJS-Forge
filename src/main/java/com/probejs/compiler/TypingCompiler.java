package com.probejs.compiler;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.probejs.ProbeJS;
import com.probejs.ProbePaths;
import com.probejs.document.DocManager;
import com.probejs.document.DocumentClass;
import com.probejs.formatter.ClassResolver;
import com.probejs.formatter.NameResolver;
import com.probejs.formatter.SpecialTypes;
import com.probejs.formatter.formatter.FormatterClass;
import com.probejs.formatter.formatter.FormatterNamespace;
import com.probejs.formatter.formatter.FormatterRaw;
import com.probejs.formatter.formatter.IFormatter;
import com.probejs.info.ClassInfo;
import com.probejs.info.EventInfo;
import com.probejs.info.SpecialData;
import com.probejs.info.Walker;
import com.probejs.info.type.TypeInfoClass;
import com.probejs.plugin.CapturedClasses;
import com.probejs.plugin.DummyBindingEvent;
import dev.latvian.kubejs.KubeJSPaths;
import dev.latvian.kubejs.recipe.RecipeTypeJS;
import dev.latvian.kubejs.server.ServerScriptManager;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

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
            .map(eventInfo -> eventInfo.clazzRaw)
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
        bindingEvent
            .getConstantDumpMap()
            .values()
            .stream()
            .map(DummyBindingEvent::touchConstantClassRecursive)
            .forEach(touchableClasses::addAll);
        touchableClasses.addAll(bindingEvent.getClassDumpReversed().keySet());

        Walker walker = new Walker(touchableClasses);
        return walker.walk();
    }

    public static void compileGlobal(Set<Class<?>> globalClasses) throws IOException {
        BufferedWriter writer = Files.newBufferedWriter(ProbePaths.GENERATED.resolve("globals.d.ts"));
        writer.write("/// <reference path=\"./special.d.ts\" />\n");
        Multimap<String, IFormatter> namespaced = ArrayListMultimap.create();

        for (Class<?> clazz : globalClasses) {
            FormatterClass formatter = new FormatterClass(ClassInfo.ofCache(clazz));
            DocManager.classDocuments.get(clazz.getName()).forEach(formatter::addDocument);

            NameResolver.ResolvedName name = NameResolver.getResolvedName(clazz.getName());
            if (name.getNamespace().isEmpty()) {
                for (String line : formatter.format(0, 4)) {
                    writer.write(line);
                    writer.write("\n");
                }
                if (clazz.isInterface()) {
                    String fullName = name.getFullName();
                    writer.write(String.format("declare const %s: %s;\n", fullName, fullName));
                }
            } else {
                formatter.setInternal(true);
                namespaced.put(name.getNamespace(), formatter);
            }
        }

        for (val entry : namespaced.asMap().entrySet()) {
            val path = entry.getKey();
            val formatters = entry.getValue();
            val namespace = new FormatterNamespace(path, formatters);
            for (val line : namespace.format(0, 4)) {
                writer.write(line);
                writer.write('\n');
            }
        }

        for (val entry : DocManager.classAdditions.entrySet()) {
            val document = entry.getValue();
            val baseDoc = document.get(0);
            document.subList(1, document.size()).forEach(baseDoc::merge);
        }

        //namespace::Document
        for (String line : new FormatterNamespace(
            "Document",
            DocManager.classAdditions.values().stream().map(l -> l.get(0)).collect(Collectors.toList())
        )
            .format(0, 4)) {
            writer.write(line);
            writer.write("\n");
        }
        //namespace::Type
        for (String line : new FormatterNamespace("Type", DocManager.typeDocuments).format(0, 4)) {
            writer.write(line);
            writer.write("\n");
        }
        //no namespace
        for (String line : new FormatterRaw(DocManager.rawTSDoc).format(0, 4)) {
            writer.write(line);
            writer.write("\n");
        }

        writer.close();
    }

    public static void compileConstants(DummyBindingEvent bindingEvent) throws IOException {
        BufferedWriter writer = Files.newBufferedWriter(ProbePaths.GENERATED.resolve("constants.d.ts"));
        writer.write("/// <reference path=\"./globals.d.ts\" />\n");
        for (Map.Entry<String, Object> entry : (
            new TreeMap<>(bindingEvent.getConstantDumpMap())
        ).entrySet()) {
            String name = entry.getKey();
            Object value = entry.getValue();
            String resolved = NameResolver.formatValue(value);
            if (resolved == null) {
                resolved = FormatterClass.formatParameterized(new TypeInfoClass(value.getClass()));
            }
            writer.write(String.format("declare const %s: %s;\n", name, resolved));
        }
        writer.close();
    }

    public static void compileJava(Set<Class<?>> globalClasses) throws IOException {
        BufferedWriter writer = Files.newBufferedWriter(ProbePaths.GENERATED.resolve("java.d.ts"));
        List<String> lines = new ArrayList<>(100);
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
                    FormatterClass.formatParameterized(new TypeInfoClass(c))
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
        val writer = Files.newBufferedWriter(KubeJSPaths.DIRECTORY.resolve("jsconfig.json"));
        val lines = String
            .join(
                "\n",
                "{",
                "    'compilerOptions': {",
                "        'lib': ['ES5', 'ES2015'],",
                "        'rootDirs': ['probe/generated', 'probe/user', 'server_scripts', 'startup_scripts', 'client_scripts'],",
                "    }",
                "}"
            )
            .replace("'", "\"");
        writer.write(lines);
        writer.close();
    }

    public static void compile() throws IOException {
        final DummyBindingEvent bindingEvent = SpecialData.computeBindingEvent();
        final Map<ResourceLocation, RecipeTypeJS> typeMap = SpecialData.computeRecipeTypes();
        final Map<String, EventInfo> knownEvents = EventCompiler.readCachedEvents();
        knownEvents.putAll(CapturedClasses.capturedEvents);
        final Map<String, Class<?>> knownRawEvents = EventCompiler.readCachedForgeEvents();
        knownRawEvents.putAll(CapturedClasses.capturedRawEvents);

        final Set<Class<?>> cachedClasses = knownEvents
            .values()
            .stream()
            .map(eventInfo -> eventInfo.clazzRaw)
            .collect(Collectors.toSet());
        cachedClasses.addAll(knownRawEvents.values());

        //global class
        final Set<Class<?>> globalClasses = fetchClasses(typeMap, bindingEvent, cachedClasses);
        globalClasses.removeIf(ClassResolver::shouldSkip);

        bindingEvent.getClassDumpReversed().forEach((c, names) -> {
            final String base = names.get(0);
            NameResolver.putResolvedName(c, base);
            for (int i = 1; i < names.size(); i++) {
                DocManager.rawTSDoc.add(String.format("declare const %s = %s", names.get(i), base));
            }
        });
        NameResolver.resolveNames(globalClasses);

        SpecialTypes.processSpecialAssignments();

        compileGlobal(globalClasses);
        SpecialCompiler.compile(typeMap);
        EventCompiler.compile(knownEvents, knownRawEvents);
        compileConstants(bindingEvent);
        compileJava(globalClasses);
        compileJSConfig();
        EventCompiler.compileEventsCache(knownEvents);
        EventCompiler.compileForgeEventsCache(knownRawEvents);
    }
}
