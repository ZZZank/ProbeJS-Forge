package com.probejs.info;

import com.probejs.compiler.special.RegistryCompiler;
import dev.latvian.kubejs.KubeJSRegistries;
import dev.latvian.kubejs.util.Tags;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagCollection;

public class SpecialData {

    public final Map<String, Collection<ResourceLocation>> tags;
    public final Map<String, Collection<ResourceLocation>> registries;
    public final Collection<RegistryInfo> infos;

    public SpecialData(
        Map<String, Collection<ResourceLocation>> tags,
        Map<String, Collection<ResourceLocation>> registries
    ) {
        this.tags = tags;
        this.registries = registries;
        this.infos = null;
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

    public static SpecialData fetch() {
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
        return new SpecialData(tags, registries);
    }

    @Override
    public String toString() {
        return "KubeDump{" + "tags=" + tags + ", registries=" + registries + '}';
    }

    public static List<RegistryInfo> getInfos() {
        return RegistryCompiler.registries
            .values()
            .stream()
            .map(RegistryInfo::new)
            .collect(Collectors.toList());
    }
}
