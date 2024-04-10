package com.probejs.compiler.special;

import com.probejs.ProbeJS;
import com.probejs.formatter.formatter.FormatterNamespace;
import com.probejs.formatter.formatter.FormatterRaw;
import com.probejs.formatter.formatter.IFormatter;
import com.probejs.info.RegistryInfo;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class RegistryCompiler {

    private static Collection<RegistryInfo> rInfos;

    public static void init(Collection<RegistryInfo> registries) {
        rInfos = registries;
    }

    public static void compile(BufferedWriter writer) throws IOException {
        Map<String, List<RegistryInfo>> infoByMods = new HashMap<>();
        for (RegistryInfo info : RegistryCompiler.rInfos) {
            infoByMods.computeIfAbsent(info.id.getNamespace(), k -> new ArrayList<>()).add(info);
        }
        List<IFormatter> formatters = new ArrayList<>();
        infoByMods.forEach((namespace, rInfo) -> {
            List<String> lines = rInfo
                .stream()
                .map(info -> {
                    List<String> names = info.names
                        .stream()
                        .map(rl -> ProbeJS.GSON.toJson(rl.toString()))
                        .collect(Collectors.toList());
                    if (names.isEmpty()) {
                        names.add("never"); //for empty registry
                    }
                    return String.format(
                        "type %s = %s;",
                        info.id.getPath().replace('/', '$'),
                        String.join("|", names)
                    );
                })
                .collect(Collectors.toList());
            formatters.add(new FormatterNamespace(namespace, new FormatterRaw(lines, false)));
        });
        IFormatter namespaced = new FormatterNamespace("Registry", formatters);
        for (String line : namespaced.format(0, 4)) {
            writer.write(line);
            writer.write('\n');
        }
        writer.write('\n');
        rInfos = null;
    }
}
