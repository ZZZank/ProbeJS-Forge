package com.probejs.compiler.special;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.probejs.ProbeJS;
import com.probejs.formatter.formatter.FormatterNamespace;
import com.probejs.formatter.formatter.FormatterRaw;
import com.probejs.formatter.formatter.IFormatter;
import com.probejs.info.RegistryInfo;
import com.probejs.info.SpecialData;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryManager;

public class RegistryCompiler {

    public static final Map<ResourceLocation, ForgeRegistry<? extends IForgeRegistryEntry<?>>> registries = new HashMap<>();

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
        registries.clear();
        try {
            Field f = RegistryManager.class.getDeclaredField("registries");
            f.setAccessible(true);

            registries.putAll(castedGet(f, RegistryManager.ACTIVE));
            registries.putAll(castedGet(f, RegistryManager.FROZEN));
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        IFormatter namespaced = new FormatterNamespace("Registry", info2Formatters(SpecialData.getInfos()));
        for (String line : namespaced.format(0, 4)) {
            writer.write(line);
            writer.write('\n');
        }
        writer.write('\n');
    }
}
