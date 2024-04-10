package com.probejs.compiler;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.probejs.ProbeJS;
import com.probejs.ProbePaths;
import com.probejs.formatter.NameResolver;
import com.probejs.info.RegistryInfo;
import com.probejs.info.SpecialData;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;

public class SnippetCompiler {

    public static JsonObject toSnippet() {
        JsonObject resultJson = new JsonObject();
        // Compile normal entries to snippet
        for (RegistryInfo info : SpecialData.computeRegistryInfos()) {
            final String type = info.id.getPath();
            final Map<String, List<String>> byModMembers = new HashMap<>();
            info.names.forEach(rl ->
                byModMembers.computeIfAbsent(rl.getNamespace(), k -> new ArrayList<>()).add(rl.getPath())
            );
            byModMembers.forEach((mod, modMembers) -> {
                final JsonObject modMembersJson = new JsonObject();
                //prefix
                final JsonArray prefixes = new JsonArray();
                if (ProbeJS.CONFIG.vanillaOrder) {
                    prefixes.add(String.format("@%s.%s", mod, type));
                } else {
                    prefixes.add(String.format("@%s.%s", type, mod));
                }
                modMembersJson.add("prefix", prefixes);
                //body
                modMembersJson.addProperty(
                    "body",
                    String.format("\"%s:${1|%s|}\"", mod, String.join(",", modMembers))
                );
                //type as name, e.g. "fluid_minecraft"
                resultJson.add(String.format("%s_%s", type, mod), modMembersJson);
            });
        }

        // Compile tag entries to snippet
        for (Map.Entry<String, Collection<ResourceLocation>> entry : SpecialData.computeTags().entrySet()) {
            String type = entry.getKey();
            Map<String, List<String>> byModMembers = new HashMap<>();
            entry
                .getValue()
                .forEach(rl ->
                    byModMembers.computeIfAbsent(rl.getNamespace(), k -> new ArrayList<>()).add(rl.getPath())
                );
            byModMembers.forEach((mod, modMembers) -> {
                final JsonObject modMembersJson = new JsonObject();
                final JsonArray prefixes = new JsonArray();
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

    public static void compile() throws IOException {
        if (ProbeJS.CONFIG.exportClassNames) {
            compileClassNames();
        }
        Path codeFile = ProbePaths.WORKSPACE.resolve("probe.code-snippets");
        BufferedWriter writer = Files.newBufferedWriter(codeFile);

        ProbeJS.GSON.toJson(toSnippet(), writer);

        writer.close();
    }

    private static void compileClassNames() throws IOException {
        JsonObject resultJson = new JsonObject();
        for (Map.Entry<String, NameResolver.ResolvedName> entry : NameResolver.resolvedNames.entrySet()) {
            final String className = entry.getKey();
            final NameResolver.ResolvedName resolvedName = entry.getValue();
            final JsonObject classJson = new JsonObject();
            final JsonArray prefix = new JsonArray();
            prefix.add(String.format("!%s", resolvedName.getFullName()));
            classJson.add("prefix", prefix);
            classJson.addProperty("body", className);
            resultJson.add(resolvedName.getFullName(), classJson);
        }

        Path codeFile = ProbePaths.WORKSPACE.resolve("classNames.code-snippets");
        BufferedWriter writer = Files.newBufferedWriter(codeFile);
        ProbeJS.GSON.toJson(resultJson, writer);
        writer.close();
    }
}
