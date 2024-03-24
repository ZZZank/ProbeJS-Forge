package com.probejs.info;

import com.probejs.compiler.special.RegistryCompiler;
import dev.latvian.kubejs.util.Tags;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagCollection;

public class SpecialData {

    public final Map<String, Collection<ResourceLocation>> tags;
    public final Collection<RegistryInfo> registries;

    public SpecialData(
        Map<String, Collection<ResourceLocation>> tags,
        Collection<RegistryInfo> registries
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

    public static SpecialData fetch() {
        final Map<String, Collection<ResourceLocation>> tags = new HashMap<>();
        putTag(tags, "items", Tags.items());
        putTag(tags, "blocks", Tags.blocks());
        putTag(tags, "fluids", Tags.fluids());
        putTag(tags, "entity_types", Tags.entityTypes());
        return new SpecialData(tags, getInfos());
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
