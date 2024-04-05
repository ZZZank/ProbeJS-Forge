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
                    List<String> names = rInfo.names
                        .stream()
                        .map(rl -> ProbeJS.GSON.toJson(rl.toString()))
                        .collect(Collectors.toList());
                    if (names.isEmpty()) {
                        //for empty registry
                        names.add("never");
                    // } else {
                    //     //fallback for general registry, to make data-driven style scripts happy
                    //     names.add("string");
                    }
                    return String.format(
                        "type %s = %s;",
                        rInfo.id.getPath().replace('/', '$'),
                        String.join("|", names)
                    );
                })
                .collect(Collectors.toList());
            formatters.add(new FormatterNamespace(namespace, new FormatterRaw(lines, false)));
        });
        return formatters;
    }

    public static void compile(BufferedWriter writer) throws IOException {
        IFormatter namespaced = new FormatterNamespace("Registry", info2Formatters(RegistryCompiler.rInfos));
        for (String line : namespaced.format(0, 4)) {
            writer.write(line);
            writer.write('\n');
        }
        writer.write('\n');
        rInfos = null;
    }
}
