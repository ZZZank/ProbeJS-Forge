package com.probejs.compiler;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.probejs.ProbeJS;
import com.probejs.ProbePaths;
import com.probejs.formatter.NameResolver;
import dev.latvian.kubejs.KubeJSRegistries;
import dev.latvian.kubejs.util.Tags;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagCollection;

public class SnippetCompiler {

    public static class KubeDump {

        public final Map<String, Map<String, List<String>>> tags;
        public final Map<String, List<String>> registries;

        public KubeDump(Map<String, Map<String, List<String>>> tags, Map<String, List<String>> registries) {
            this.tags = tags;
            this.registries = registries;
        }

        private static void putTag(
            Map<String, Map<String, List<String>>> tags,
            String type,
            TagCollection<?> tagCollection
        ) {
            // tags.put(
            //     type,
            //     /* List<String> */
            //     FormatterTag
            //         .getTagsFromRegistry(tagCollection)
            //         .stream()
            //         .collect(Collectors.toMap(s -> s, s -> new ArrayList<>()))
            // );
            tags.put(
                type,
                tagCollection
                    .getAvailableTags()
                    .stream()
                    .map(ResourceLocation::toString)
                    .collect(Collectors.toMap(name -> name, name -> new ArrayList<>()))
            );
        }

        private static <T> void putRegistry(
            Map<String, List<String>> registries,
            String type,
            ResourceKey<Registry<T>> registry
        ) {
            registries.put(
                type,
                KubeJSRegistries
                    .genericRegistry(registry)
                    .getIds()
                    .stream()
                    .map(ResourceLocation::toString)
                    .collect(Collectors.toList())
            );
        }

        public static KubeDump fetch() {
            Map<String, Map<String, List<String>>> tags = new HashMap<>();
            Map<String, List<String>> registries = new HashMap<>();
            putTag(tags, "items", Tags.items());
            putTag(tags, "blocks", Tags.blocks());
            putTag(tags, "fluids", Tags.fluids());
            putTag(tags, "entity_types", Tags.entityTypes());
            putRegistry(registries, "items", Registry.ITEM_REGISTRY);
            putRegistry(registries, "blocks", Registry.BLOCK_REGISTRY);
            putRegistry(registries, "fluids", Registry.FLUID_REGISTRY);
            putRegistry(registries, "entity_types", Registry.ENTITY_TYPE_REGISTRY);
            return new KubeDump(tags, registries);
        }

        @Override
        public String toString() {
            return "KubeDump{" + "tags=" + tags + ", registries=" + registries + '}';
        }

        public JsonObject toSnippet() {
            JsonObject resultJson = new JsonObject();
            // Compile normal entries to snippet
            for (Map.Entry<String, List<String>> entry : this.registries.entrySet()) {
                String type = entry.getKey();
                Map<String, List<String>> byModMembers = new HashMap<>();
                entry
                    .getValue()
                    .stream()
                    .map(rl -> rl.split(":", 2))
                    .forEach(rl -> byModMembers.computeIfAbsent(rl[0], k -> new ArrayList<>()).add(rl[1]));
                byModMembers.forEach((mod, modMembers) -> {
                    JsonObject modMembersJson = new JsonObject();
                    JsonArray prefixes = new JsonArray();
                    if (ProbeJS.CONFIG.vanillaOrder) {
                        prefixes.add(String.format("@%s.%s", mod, type));
                    } else {
                        prefixes.add(String.format("@%s.%s", type, mod));
                    }
                    modMembersJson.add("prefix", prefixes);
                    modMembersJson.addProperty(
                        "body",
                        String.format("\"%s:${1|%s|}\"", mod, String.join(",", modMembers))
                    );
                    resultJson.add(String.format("%s_%s", type, mod), modMembersJson);
                });
            }

            // Compile tag entries to snippet
            for (Map.Entry<String, Map<String, List<String>>> entry : this.tags.entrySet()) {
                String type = entry.getKey();
                Map<String, List<String>> byModMembers = new HashMap<>();
                entry
                    .getValue()
                    .keySet()
                    .stream()
                    .map(rl -> rl.split(":", 2))
                    .forEach(rl -> byModMembers.computeIfAbsent(rl[0], k -> new ArrayList<>()).add(rl[1]));
                byModMembers.forEach((mod, modMembers) -> {
                    JsonObject modMembersJson = new JsonObject();
                    JsonArray prefixes = new JsonArray();
                    if (ProbeJS.CONFIG.vanillaOrder) {
                        prefixes.add(String.format("@%s.tags.%s", mod, type));
                    } else {
                        prefixes.add(String.format("@%s.tags.%s", type, mod));
                    }
                    modMembersJson.add("prefix", prefixes);
                    modMembersJson.addProperty(
                        "body",
                        String.format("\"#%s:${1|%s|}\"", mod, String.join(",", modMembers))
                    );
                    resultJson.add(String.format("%s_tag_%s", type, mod), modMembersJson);
                });
            }

            return resultJson;
        }
    }

    public static void compile() throws IOException {
        if (ProbeJS.CONFIG.exportClassNames) {
            compileClassNames();
        }
        writeDumpSnippets();
    }

    private static void writeDumpSnippets() throws IOException {
        Path codeFile = ProbePaths.SNIPPET.resolve("probe.code-snippets");
        KubeDump kubeDump = KubeDump.fetch();

        BufferedWriter writer = Files.newBufferedWriter(codeFile);
        writer.write(ProbeJS.GSON.toJson(kubeDump.toSnippet()));
        writer.flush();
        writer.close();
    }

    private static void compileClassNames() throws IOException {
        JsonObject resultJson = new JsonObject();
        for (Map.Entry<String, NameResolver.ResolvedName> entry : NameResolver.resolvedNames.entrySet()) {
            String className = entry.getKey();
            NameResolver.ResolvedName resolvedName = entry.getValue();
            JsonObject classJson = new JsonObject();
            JsonArray prefix = new JsonArray();
            prefix.add(String.format("!%s", resolvedName.getFullName()));
            classJson.add("prefix", prefix);
            classJson.addProperty("body", className);
            resultJson.add(resolvedName.getFullName(), classJson);
        }

        Path codeFile = ProbePaths.SNIPPET.resolve("classNames.code-snippets");
        BufferedWriter writer = Files.newBufferedWriter(codeFile);
        ProbeJS.GSON.toJson(resultJson, writer);
        writer.flush();
    }
}
