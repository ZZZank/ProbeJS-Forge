package com.probejs.info;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import dev.latvian.kubejs.util.Tags;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagCollection;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryManager;

public class SpecialData {

    public final Map<String, Collection<ResourceLocation>> tags;
    public final Collection<RegistryInfo> registries;

    public SpecialData(Map<String, Collection<ResourceLocation>> tags, Collection<RegistryInfo> registries) {
        this.tags = tags;
        this.registries = registries;
    }

    private static void putTag(
        Map<String, Collection<ResourceLocation>> target,
        String type,
        TagCollection<?> tagCollection
    ) {
        target.put(type, tagCollection.getAvailableTags().stream().sorted().collect(Collectors.toList()));
    }

    public static SpecialData fetch() {
        final Map<String, Collection<ResourceLocation>> tags = new HashMap<>();
        putTag(tags, "items", Tags.items());
        putTag(tags, "blocks", Tags.blocks());
        putTag(tags, "fluids", Tags.fluids());
        putTag(tags, "entity_types", Tags.entityTypes());
        return new SpecialData(tags, computeRegistryInfos());
    }

    @Override
    public String toString() {
        return String.format("SpecialData{tags=%s, registries=%s}", tags, registries);
    }

    public static List<RegistryInfo> computeRegistryInfos() {
        return fetchRawRegistries().values().stream().map(RegistryInfo::new).collect(Collectors.toList());
    }

    private static Map<ResourceLocation, ForgeRegistry<? extends IForgeRegistryEntry<?>>> fetchRawRegistries() {
        Map<ResourceLocation, ForgeRegistry<? extends IForgeRegistryEntry<?>>> registries = new HashMap<>();
        try {
            Field f = RegistryManager.class.getDeclaredField("registries");
            f.setAccessible(true);

            registries.putAll(castedGet(f, RegistryManager.ACTIVE));
            registries.putAll(castedGet(f, RegistryManager.FROZEN));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return registries;
    }

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
}
