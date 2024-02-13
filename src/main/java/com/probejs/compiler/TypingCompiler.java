package com.probejs.compiler;

import com.probejs.ProbePaths;
import com.probejs.document.DocumentClass;
import com.probejs.document.Manager;
import com.probejs.formatter.ClassResolver;
import com.probejs.formatter.NameResolver;
import com.probejs.formatter.SpecialTypes;
import com.probejs.formatter.formatter.FormatterClass;
import com.probejs.formatter.formatter.FormatterNamespace;
import com.probejs.formatter.formatter.FormatterRawTS;
import com.probejs.formatter.formatter.IFormatter;
import com.probejs.info.ClassInfo;
import com.probejs.info.EventInfo;
import com.probejs.info.Walker;
import com.probejs.info.type.TypeInfoClass;
import com.probejs.plugin.CapturedClasses;
import com.probejs.recipe.RecipeHolders;
import dev.latvian.kubejs.KubeJSPaths;
import dev.latvian.kubejs.recipe.RecipeTypeJS;
import dev.latvian.kubejs.recipe.RegisterRecipeHandlersEvent;
import dev.latvian.kubejs.server.ServerScriptManager;
import dev.latvian.kubejs.util.KubeJSPlugins;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
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
            .map(eventInfo -> eventInfo.captured)
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
        Map<String, List<IFormatter>> namespaced = new HashMap<>();

        for (Class<?> clazz : globalClasses) {
            FormatterClass formatter = new FormatterClass(ClassInfo.ofCache(clazz));
            Manager.classDocuments
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

        for (Map.Entry<String, List<DocumentClass>> entry : Manager.classAdditions.entrySet()) {
            List<DocumentClass> document = entry.getValue();
            DocumentClass start = document.get(0);
            document.subList(1, document.size()).forEach(start::merge);
        }

        //Doc
        for (String line : new FormatterNamespace(
            "Document",
            Manager.classAdditions.values().stream().map(l -> l.get(0)).collect(Collectors.toList())
        )
            .format(0, 4)) {
            writer.write(line);
            writer.write("\n");
        }
        //namespace::Type
        for (String line : new FormatterNamespace("Type", Manager.typeDocuments).format(0, 4)) {
            writer.write(line);
            writer.write("\n");
        }
        //namespace::TSDoc
        for (String line : new FormatterRawTS(Manager.rawTSDoc).format(0, 4)) {
            writer.write(line);
            writer.write("\n");
        }

        writer.flush();
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
                resolved = FormatterClass.formatTypeParameterized(new TypeInfoClass(value.getClass()));
            }
            writer.write(String.format("declare const %s: %s;\n", name, resolved));
        }
        writer.flush();
        writer.close();
    }

    public static void compileJava(Set<Class<?>> globalClasses) throws IOException {
        BufferedWriter writer = Files.newBufferedWriter(ProbePaths.GENERATED.resolve("java.d.ts"));
        writer.write("/// <reference path=\"./globals.d.ts\" />\n");
        for (Class<?> c : globalClasses) {
            if (ServerScriptManager.instance.scriptManager.isClassAllowed(c.getName())) {
                writer.write(
                    String.format(
                        "declare function java(name: \"%s\"): typeof %s;\n",
                        c.getName(),
                        FormatterClass.formatTypeParameterized(new TypeInfoClass(c))
                    )
                );
            }
        }
        writer.flush();
        writer.close();
    }

    public static void compileRecipeHolder(Map<ResourceLocation, RecipeTypeJS> typeMap) throws IOException {
        RecipeHolders.init(typeMap);
        BufferedWriter writer = Files.newBufferedWriter(
            ProbePaths.GENERATED.resolve("globals.d.ts"),
            StandardOpenOption.APPEND
        );
        for (String line : RecipeHolders.format(0, 4)) {
            writer.write(line);
            writer.write('\n');
        }
        writer.flush();
        writer.close();
    }

    public static void compileJSConfig() throws IOException {
        BufferedWriter writer = Files.newBufferedWriter(KubeJSPaths.DIRECTORY.resolve("jsconfig.json"));
        String lines = String.join(
            "\n",
            "{",
            "    \"compilerOptions\": {",
            "        \"lib\": [\"ES5\", \"ES2015\"],",
            "        \"typeRoots\": [\"./probe/generated\", \"./probe/user\"]",
            "    }",
            "}"
        );
        writer.write(lines);
        writer.flush();
        writer.close();
    }

    public static void compile() throws IOException {
        DummyBindingEvent bindingEvent = new DummyBindingEvent(ServerScriptManager.instance.scriptManager);
        Map<ResourceLocation, RecipeTypeJS> typeMap = new HashMap<>();
        RegisterRecipeHandlersEvent recipeEvent = new RegisterRecipeHandlersEvent(typeMap);

        KubeJSPlugins.forEachPlugin(plugin -> plugin.addRecipes(recipeEvent));
        KubeJSPlugins.forEachPlugin(plugin -> plugin.addBindings(bindingEvent));
        //cache class
        Map<String, EventInfo> cachedEvents = EventCompiler.readCachedEvents("cachedEvents.json");
        Map<String, Class<?>> cachedForgeEvents = EventCompiler.readCachedForgeEvents("cachedForgeEvents.json");
        Set<Class<?>> cachedClasses = new HashSet<>(
            cachedEvents.values().stream().map(eventInfo -> eventInfo.captured).collect(Collectors.toList())
        );
        cachedClasses.addAll(cachedForgeEvents.values());
        // cachedClasses.addAll(RegistryCompiler.getRegistryClasses());
        //global class
        Set<Class<?>> globalClasses = fetchClasses(typeMap, bindingEvent, cachedClasses);
        globalClasses.removeIf(c -> ClassResolver.skipped.contains(c));
        SpecialTypes.processFunctionalInterfaces(globalClasses);
        compileGlobal(bindingEvent, globalClasses);
        // RegistryCompiler.compileRegistries();
        EventCompiler.compileEvents(cachedEvents, cachedForgeEvents);
        compileConstants(bindingEvent);
        compileJava(globalClasses);
        compileRecipeHolder(typeMap);
        compileJSConfig();
        EventCompiler.writeEvents2Cache("cachedEvents.json", cachedEvents);
        EventCompiler.writeForgeEvents2Cache("cachedForgeEvents.json", cachedForgeEvents);
    }
}
