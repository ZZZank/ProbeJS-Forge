package com.probejs.compiler;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.probejs.ProbeConfig;
import com.probejs.ProbeJS;
import com.probejs.ProbePaths;
import com.probejs.formatter.NameResolver;
import dev.latvian.kubejs.KubeJSPaths;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SnippetCompiler {

    public static class KubeDump {

        public Map<String, Map<String, List<String>>> tags;
        public Map<String, List<String>> registries;

        public KubeDump(Map<String, Map<String, List<String>>> tags, Map<String, List<String>> registries) {
            this.tags = tags;
            this.registries = registries;
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
                List<String> members = entry.getValue();
                Map<String, List<String>> byModMembers = new HashMap<>();
                members
                    .stream()
                    .map(rl -> rl.split(":"))
                    .forEach(rl -> {
                        if (!byModMembers.containsKey(rl[0])) {byModMembers.put(rl[0], new ArrayList<>());}
                        byModMembers.get(rl[0]).add(rl[1]);
                    });
                byModMembers.forEach((mod, modMembers) -> {
                    JsonObject modMembersJson = new JsonObject();
                    JsonArray prefixes = new JsonArray();
                    if (ProbeConfig.INSTANCE.vanillaOrder) {
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
                List<String> members = entry.getValue().keySet().stream().collect(Collectors.toList());
                Map<String, List<String>> byModMembers = new HashMap<>();
                members
                    .stream()
                    .map(rl -> rl.split(":", 2))
                    .forEach(rl -> {
                        if (!byModMembers.containsKey(rl[0])) {
                            byModMembers.put(rl[0], new ArrayList<>());
                        }
                        byModMembers.get(rl[0]).add(rl[1]);
                    });
                byModMembers.forEach((mod, modMembers) -> {
                    JsonObject modMembersJson = new JsonObject();
                    JsonArray prefixes = new JsonArray();
                    if (ProbeConfig.INSTANCE.vanillaOrder) {
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
        Path kubePath = KubeJSPaths.EXPORTED.resolve("kubejs-server-export.json");
        if (!kubePath.toFile().canRead()) {
            ProbeJS.LOGGER.error("'kubejs-server-export.json' not found!");
            return;
        }
        Path codeFile = ProbePaths.SNIPPET.resolve("probe.code-snippets");
        Gson gson = new Gson();
        Reader reader = Files.newBufferedReader(kubePath);
        KubeDump kubeDump = gson.fromJson(reader, KubeDump.class);
        BufferedWriter writer = Files.newBufferedWriter(codeFile);
        writer.write(gson.toJson(kubeDump.toSnippet()));
        writer.flush();
        writer.close();
    }

    public static void compileClassNames() throws IOException {
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
        Gson gson = new Gson();
        BufferedWriter writer = Files.newBufferedWriter(codeFile);
        gson.toJson(resultJson, writer);
        writer.flush();
    }
}
