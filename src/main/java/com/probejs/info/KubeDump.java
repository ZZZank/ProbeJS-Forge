package com.probejs.info;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import dev.latvian.kubejs.KubeJSRegistries;
import dev.latvian.kubejs.util.Tags;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagCollection;

public class KubeDump {

    public final Map<String, Collection<ResourceLocation>> tags;
    public final Map<String, Collection<ResourceLocation>> registries;

    public KubeDump(
        Map<String, Collection<ResourceLocation>> tags,
        Map<String, Collection<ResourceLocation>> registries
    ) {
        this.tags = tags;
        this.registries = registries;
    }

    private static void putTag(
        Map<String, Collection<ResourceLocation>> target,
        String type,
        TagCollection<?> tagCollection
    ) {
        target.put(type, tagCollection.getAvailableTags());
    }

    private static <T> void putRegistry(
        Map<String, Collection<ResourceLocation>> target,
        String type,
        ResourceKey<Registry<T>> registryKey
    ) {
        target.put(type, KubeJSRegistries.genericRegistry(registryKey).getIds());
    }

    public static KubeDump fetch() {
        final Map<String, Collection<ResourceLocation>> tags = new HashMap<>();
        putTag(tags, "items", Tags.items());
        putTag(tags, "blocks", Tags.blocks());
        putTag(tags, "fluids", Tags.fluids());
        putTag(tags, "entity_types", Tags.entityTypes());
        final Map<String, Collection<ResourceLocation>> registries = new HashMap<>();
        putRegistry(registries, "items", Registry.ITEM_REGISTRY);
        putRegistry(registries, "blocks", Registry.BLOCK_REGISTRY);
        putRegistry(registries, "fluids", Registry.FLUID_REGISTRY);
        putRegistry(registries, "entity_types", Registry.ENTITY_TYPE_REGISTRY);
        return new KubeDump(tags, registries);
    }

    @Override
    public String toString() {
        return "KubeDump{" + "tags=" + tags + ", registries=" + registries + '}';
    }
}