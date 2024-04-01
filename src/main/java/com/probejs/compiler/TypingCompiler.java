package com.probejs.compiler;

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
import com.probejs.formatter.formatter.FormatterType;
import com.probejs.formatter.formatter.IFormatter;
import com.probejs.info.ClassInfo;
import com.probejs.info.EventInfo;
import com.probejs.info.Walker;
import com.probejs.info.type.TypeInfoClass;
import com.probejs.plugin.CapturedClasses;
import com.probejs.plugin.DummyBindingEvent;
import dev.latvian.kubejs.KubeJSPaths;
import dev.latvian.kubejs.recipe.RecipeTypeJS;
import dev.latvian.kubejs.recipe.RegisterRecipeHandlersEvent;
import dev.latvian.kubejs.server.ServerScriptManager;
import dev.latvian.kubejs.util.KubeJSPlugins;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;
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
            .map(Object::getClass)
            .forEach(touchableClasses::add);
        bindingEvent.getClassDumpMap().values().forEach(touchableClasses::add);

        Walker walker = new Walker(touchableClasses);
        return walker.walk();
    }

    public static void compileGlobal(DummyBindingEvent bindingEvent, Set<Class<?>> globalClasses)
        throws IOException {
        bindingEvent.getClassDumpMap().forEach((s, c) -> NameResolver.putResolvedName(c, s));
        NameResolver.resolveNames(globalClasses);

        BufferedWriter writer = Files.newBufferedWriter(ProbePaths.GENERATED.resolve("globals.d.ts"));
        writer.write("/// <reference path=\"./special.d.ts\" />\n");
        Map<String, List<IFormatter>> namespaced = new HashMap<>();

        for (Class<?> clazz : globalClasses) {
            FormatterClass formatter = new FormatterClass(ClassInfo.ofCache(clazz));
            DocManager.classDocuments
                .getOrDefault(clazz.getName(), new ArrayList<>())
                .forEach(formatter::addDocument);

            NameResolver.ResolvedName name = NameResolver.getResolvedName(clazz.getName());
            if (name.getNamespace().isEmpty()) {
                for (String line : formatter.format(0, 4)) {
                    writer.write(line);
                    writer.write("\n");
                }
                if (clazz.isInterface()) {
                    writer.write(
                        String.format("declare const %s: %s;\n", name.getFullName(), name.getFullName())
                    );
                }
            } else {
                formatter.setInternal(true);
                namespaced.computeIfAbsent(name.getNamespace(), s -> new ArrayList<>()).add(formatter);
            }
        }

        for (Map.Entry<String, List<IFormatter>> entry : namespaced.entrySet()) {
            String path = entry.getKey();
            List<IFormatter> formatters = entry.getValue();
            FormatterNamespace namespace = new FormatterNamespace(path, formatters);
            writer.write(String.join("\n", namespace.format(0, 4)) + "\n");
        }

        for (Map.Entry<String, List<DocumentClass>> entry : DocManager.classAdditions.entrySet()) {
            List<DocumentClass> document = entry.getValue();
            DocumentClass start = document.get(0);
            document.subList(1, document.size()).forEach(start::merge);
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
                resolved = FormatterType.formatParameterized(new TypeInfoClass(value.getClass()));
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
            .sorted(Comparator.comparing(c -> c.getName()))
            .map(c ->
                String.format(
                    "declare function java(name: %s): typeof %s;",
                    ProbeJS.GSON.toJson(c.getName()),
                    FormatterType.formatParameterized(new TypeInfoClass(c))
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
        BufferedWriter writer = Files.newBufferedWriter(KubeJSPaths.DIRECTORY.resolve("jsconfig.json"));
        String lines = String
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
        final DummyBindingEvent bindingEvent = new DummyBindingEvent(
            ServerScriptManager.instance.scriptManager
        );
        final Map<ResourceLocation, RecipeTypeJS> typeMap = new HashMap<>();
        final RegisterRecipeHandlersEvent recipeEvent = new RegisterRecipeHandlersEvent(typeMap);

        KubeJSPlugins.forEachPlugin(plugin -> plugin.addRecipes(recipeEvent));
        KubeJSPlugins.forEachPlugin(plugin -> plugin.addBindings(bindingEvent));

        //event cache
        final Map<String, EventInfo> cachedEvents = EventCompiler.readCachedEvents();
        final Map<String, Class<?>> cachedForgeEvents = EventCompiler.readCachedForgeEvents();
        cachedEvents.putAll(CapturedClasses.capturedEvents);
        cachedForgeEvents.putAll(CapturedClasses.capturedRawEvents);

        final Set<Class<?>> cachedClasses = cachedEvents
            .values()
            .stream()
            .map(eventInfo -> eventInfo.clazzRaw)
            .collect(Collectors.toSet());
        cachedClasses.addAll(cachedForgeEvents.values());
        // cachedClasses.addAll(RegistryCompiler.getRegistryClasses());

        //global class
        final Set<Class<?>> globalClasses = fetchClasses(typeMap, bindingEvent, cachedClasses);

        globalClasses.removeIf(ClassResolver.skipped::contains);
        SpecialTypes.processSpecialAssignments();
        SpecialCompiler.init(typeMap);

        compileGlobal(bindingEvent, globalClasses);
        SpecialCompiler.compile();
        EventCompiler.compileEvents(cachedEvents, cachedForgeEvents);
        compileConstants(bindingEvent);
        compileJava(globalClasses);
        compileJSConfig();
        EventCompiler.compileEventsCache(cachedEvents);
        EventCompiler.comileForgeEventsCache(cachedForgeEvents);
    }
}
