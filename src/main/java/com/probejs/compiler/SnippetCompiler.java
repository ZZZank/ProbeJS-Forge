package com.probejs.compiler;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.probejs.ProbeJS;
import com.probejs.ProbePaths;
import com.probejs.formatter.resolver.NameResolver;
import com.probejs.info.RegistryInfo;
import com.probejs.info.SpecialData;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import com.probejs.util.json.JArray;
import com.probejs.util.json.JObject;
import lombok.val;
import net.minecraft.resources.ResourceLocation;

public class SnippetCompiler {

    public static JsonObject toSnippet() {
        val resultJson = JObject.of();
        // Compile normal entries to snippet
        generateRegistrySnippet(resultJson);

        // Compile tag entries to snippet
        generateTagSnippet(resultJson);

        return resultJson.build();
    }

    private static void generateRegistrySnippet(JObject resultJson) {
        val paths = new HashSet<String>();
        for (RegistryInfo info : SpecialData.instance().registries) {
            String type = info.id.getPath();
            if (paths.contains(type)) {
                type = String.format("%s_%s", type, info.id.getNamespace());
            } else {
                paths.add(type);
            }
            resultJson.add(
                type,
                JObject.of()
                    .add("prefix", JArray.of().add("@" + type))
                    .add(
                        "body",
                        String.format(
                            "\"${1|%s|}\"",
                            info.names.stream().map(ResourceLocation::toString).collect(Collectors.joining(","))
                        )
                    )
            );
        }
    }

    private static void generateTagSnippet(JObject resultJson) {
        val duped = new HashSet<String>();
        for (val entry : SpecialData.instance().tags.entrySet()) {
            String type = String.format("%s_tag", entry.getKey().getPath());
            if (duped.contains(type)) {
                type = type + "_" + entry.getKey().getNamespace();
            }
            duped.add(type);
            resultJson.add(
                type,
                JObject.of()
                    .add("prefix", JArray.of().add("@" + type))
                    .add(
                        "body",
                        String.format(
                            "\"#${1|%s|}\"",
                            entry.getValue().stream().map(ResourceLocation::toString).collect(Collectors.joining(","))
                        )
                    )
            );
        }
    }

    public static void compile() throws IOException {
        if (ProbeJS.CONFIG.exportClassNames) {
            compileClassNames();
        }
        val codeFile = ProbePaths.WORKSPACE.resolve("probe.code-snippets");
        val writer = Files.newBufferedWriter(codeFile);

        val snippet = toSnippet();
        ProbeJS.GSON.toJson(snippet, writer);

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
