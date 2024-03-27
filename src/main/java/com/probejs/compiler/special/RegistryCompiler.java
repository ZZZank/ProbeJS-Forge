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

    private static List<IFormatter> info2Formatters(Collection<RegistryInfo> infos) {
        Map<String, List<RegistryInfo>> infoByMods = new HashMap<>();
        for (RegistryInfo info : infos) {
            infoByMods.computeIfAbsent(info.id.getNamespace(), k -> new ArrayList<>()).add(info);
        }
        List<IFormatter> formatters = new ArrayList<>();
        infoByMods.forEach((namespace, rInfos) -> {
            List<String> lines = rInfos
                .stream()
                .map(rInfo -> {
                    String names = rInfo.names
                        .stream()
                        .map(rl -> ProbeJS.GSON.toJson(rl.toString()))
                        .collect(Collectors.joining("|"));
                    if (names.isEmpty()) {
                        names = "never";
                    }
                    return String.format("type %s = %s;", rInfo.id.getPath().replace('/', '$'), names);
                })
                .collect(Collectors.toList());
            formatters.add(new FormatterNamespace(namespace, Arrays.asList(new FormatterRaw(lines, false))));
        });
        return formatters;
    }

    public static void compileRegistries(BufferedWriter writer) throws IOException {
        IFormatter namespaced = new FormatterNamespace("Registry", info2Formatters(RegistryCompiler.rInfos));
        for (String line : namespaced.format(0, 4)) {
            writer.write(line);
            writer.write('\n');
        }
        writer.write('\n');
        rInfos = null;
    }
}
