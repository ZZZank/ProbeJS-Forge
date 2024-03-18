package com.probejs.compiler;

import com.mojang.brigadier.context.CommandContext;
import com.probejs.ProbeJS;
import com.probejs.ProbePaths;
import com.probejs.formatter.formatter.FormatterNamespace;
import com.probejs.formatter.formatter.FormatterRaw;
import com.probejs.formatter.formatter.IFormatter;
import com.probejs.info.RegistryInfo;
import com.probejs.mixin.RegistryAccessMixin;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistryAccess.RegistryData;
import net.minecraft.resources.ResourceKey;

public class RegistryCompiler {

    private static Map<ResourceKey<? extends Registry<?>>, RegistryData<?>> registries;

    public static void init(CommandContext<CommandSourceStack> context) {
        RegistryAccess access = context.getSource().getServer().registryAccess();
        RegistryCompiler.registries = ((RegistryAccessMixin) (Object) access).GET_REGISTRIES();
    }

    public static List<RegistryInfo> getAll() {
        // RegistryCompiler.registries.keySet();
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
