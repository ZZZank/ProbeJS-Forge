package com.probejs.compiler;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.probejs.ProbeJS;
import com.probejs.ProbePaths;
import com.probejs.formatter.resolver.PathResolver;
import com.probejs.formatter.resolver.ClassPath;
import com.probejs.info.RegistryInfo;
import com.probejs.info.SpecialData;

import java.io.IOException;
import java.nio.file.Files;
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
        val duped = new HashSet<String>();
        for (RegistryInfo info : SpecialData.instance().registries()) {
            String type = info.id().getPath();
            if (duped.contains(type)) {
                type = String.format("%s_%s", type, info.id().getNamespace());
            } else {
                duped.add(type);
            }
            resultJson.add(
                type,
                JObject.of()
                    .add("prefix", JArray.of().add("@" + type))
                    .add(
                        "body",
                        String.format(
                            "\"${1|%s|}\"",
                            info.names().stream().map(ResourceLocation::toString).collect(Collectors.joining(","))
                        )
                    )
            );
        }
    }

    private static void generateTagSnippet(JObject resultJson) {
        val duped = new HashSet<String>();
        for (val entry : SpecialData.instance().tags().entrySet()) {
            //type name
            String type = String.format("%s_tag", entry.getKey().getPath());
            if (duped.contains(type)) {
                type = type + "_" + entry.getKey().getNamespace();
            }
            duped.add(type);
            //entries in it
            val names = entry.getValue().stream().map(ResourceLocation::toString).collect(Collectors.joining(","));
            if (names.isEmpty()) {
                continue;
            }
            //add
            resultJson.add(
                type,
                JObject.of()
                    .add("prefix", JArray.of().add("@" + type))
                    .add("body", String.format("\"#${1|%s|}\"", names))
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
        for (Map.Entry<String, ClassPath> entry : PathResolver.resolved.entrySet()) {
            val className = entry.getKey();
            val resolvedName = entry.getValue();
            val classJson = new JsonObject();
            val prefix = new JsonArray();
            prefix.add(String.format("!%s", resolvedName.fullPath()));
            classJson.add("prefix", prefix);
            classJson.addProperty("body", className);
            resultJson.add(resolvedName.fullPath(), classJson);
        }

        val codeFile = ProbePaths.WORKSPACE.resolve("classNames.code-snippets");
        val writer = Files.newBufferedWriter(codeFile);
        ProbeJS.GSON.toJson(resultJson, writer);
        writer.close();
    }
}
