package com.probejs.compiler;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.probejs.ProbeJS;
import com.probejs.ProbePaths;
import com.probejs.formatter.formatter.FormatterNamespace;
import com.probejs.formatter.formatter.FormatterRaw;
import com.probejs.formatter.formatter.IFormatter;
import com.probejs.info.RegistryInfo;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryManager;

public class RegistryCompiler {

    private static Map<ResourceLocation, ForgeRegistry<? extends IForgeRegistryEntry<?>>> registries;

    @SuppressWarnings("unchecked")
    private static BiMap<ResourceLocation, ForgeRegistry<? extends IForgeRegistryEntry<?>>> castedGet(
        Field f,
        Object o
    ) {
        try {
            return (BiMap<ResourceLocation, ForgeRegistry<? extends IForgeRegistryEntry<?>>>) f.get(o);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return HashBiMap.create();
    }

    public static void init() {
        BiMap<ResourceLocation, ForgeRegistry<? extends IForgeRegistryEntry<?>>> m = null;
        try {
            Field f = RegistryManager.class.getDeclaredField("registries");
            f.setAccessible(true);

            m = castedGet(f, RegistryManager.ACTIVE);
            castedGet(f, RegistryManager.VANILLA).forEach(m::putIfAbsent);
            castedGet(f, RegistryManager.FROZEN).forEach(m::putIfAbsent);
        } catch (Exception e) {
            e.printStackTrace();
            m = HashBiMap.create();
        }
        RegistryCompiler.registries = m;
    }

    public static List<RegistryInfo> getInfos() {
        return RegistryCompiler.registries
            .values()
            .stream()
            .map(RegistryInfo::new)
            .collect(Collectors.toList());
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

    public static void compileRegistries() throws IOException {
        BufferedWriter writer = Files.newBufferedWriter(ProbePaths.GENERATED.resolve("special.d.ts"));
        IFormatter namespaced = new FormatterNamespace("Registry", info2Formatters(getInfos()));
        for (String line : namespaced.format(0, 4)) {
            writer.write(line);
            writer.write('\n');
        }
        writer.write('\n');
        writer.flush();
        writer.close();
    }
}
