package com.probejs.compiler.special;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.probejs.ProbeJS;
import com.probejs.formatter.FormatterNamespace;
import com.probejs.formatter.FormatterRaw;
import com.probejs.formatter.api.IFormatter;
import com.probejs.info.RegistryInfo;
import lombok.val;

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
        Multimap<String, RegistryInfo> infoByMods = ArrayListMultimap.create();
        for (RegistryInfo info : RegistryCompiler.rInfos) {
            infoByMods.put(info.id.getNamespace(), info);
        }
        List<IFormatter> formatters = new ArrayList<>();
        infoByMods.asMap().forEach((namespace, rInfo) -> {
            val lines = rInfo
                .stream()
                .map(info -> {
                    val names = info.names
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
        val namespaced = new FormatterNamespace("Registry", formatters);
        for (val line : namespaced.format(0, 4)) {
            writer.write(line);
            writer.write('\n');
        }
        writer.write('\n');
        rInfos = null;
    }
}
