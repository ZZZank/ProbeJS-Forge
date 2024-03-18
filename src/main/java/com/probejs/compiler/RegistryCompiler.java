package com.probejs.compiler;

import com.probejs.ProbeJS;
import com.probejs.ProbePaths;
import com.probejs.formatter.formatter.FormatterNamespace;
import com.probejs.formatter.formatter.FormatterRaw;
import com.probejs.formatter.formatter.IFormatter;
import com.probejs.info.RegistryInfo;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;
import net.minecraft.core.Registry;

public class RegistryCompiler {

    public static List<RegistryInfo> getAll() {
        return Registry.REGISTRY.stream().map(RegistryInfo::new).collect(Collectors.toList());
    }

    public static List<IFormatter> info2Formatters(Collection<RegistryInfo> infos) {
        Map<String, List<RegistryInfo>> infoByMods = new HashMap<>();
        for (RegistryInfo info : infos) {
            infoByMods.computeIfAbsent(info.id.getNamespace(), k -> new ArrayList<>()).add(info);
        }
        List<IFormatter> formatters = new ArrayList<>();
        infoByMods.forEach((namespace, rInfos) -> {
            List<String> lines = rInfos
                .stream()
                .map(rInfo ->
                    String.format(
                        "type %s = %s;",
                        rInfo.id.getPath().replace('/', '$'),
                        rInfo.names
                            .stream()
                            .map(rl -> ProbeJS.GSON.toJson(rl.toString()))
                            .collect(Collectors.joining("|"))
                    )
                )
                .collect(Collectors.toList());
            formatters.add(new FormatterNamespace(namespace, Arrays.asList(new FormatterRaw(lines, false))));
        });
        return formatters;
    }

    public static void compileRegistries() throws IOException {
        BufferedWriter writer = Files.newBufferedWriter(ProbePaths.GENERATED.resolve("registries.d.ts"));
        writer.write("/// <reference path=\"./globals.d.ts\" />\n");
        IFormatter namespace = new FormatterNamespace(
            "Registry",
            // RegistryObjectBuilderTypes.MAP
            //     .values()
            //     .stream()
            //     .map(FormatterRegistry::new)
            //     .collect(Collectors.toList())
            info2Formatters(getAll())
        );
        writer.write(String.join("\n", namespace.format(0, 4)));
        writer.flush();
        writer.close();
    }
}
