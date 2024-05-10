package com.probejs.info;

import com.google.common.collect.HashBiMap;
import com.probejs.capture.DummyBindingEvent;
import com.probejs.util.PUtil;
import dev.latvian.kubejs.recipe.RecipeTypeJS;
import dev.latvian.kubejs.recipe.RegisterRecipeHandlersEvent;
import dev.latvian.kubejs.server.ServerScriptManager;
import dev.latvian.kubejs.util.KubeJSPlugins;
import dev.latvian.kubejs.util.Tags;
import java.lang.reflect.Field;
import java.util.ArrayList;
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
        List<ResourceLocation> tagIds = new ArrayList<>(tagCollection.getAvailableTags());
        tagIds.sort(null);
        target.put(type, tagIds);
    }

    public static Map<String, Collection<ResourceLocation>> computeTags() {
        final Map<String, Collection<ResourceLocation>> tags = new HashMap<>();
        putTag(tags, "items", Tags.items());
        putTag(tags, "blocks", Tags.blocks());
        putTag(tags, "fluids", Tags.fluids());
        putTag(tags, "entity_types", Tags.entityTypes());
        return tags;
    }

    public static SpecialData fetch() {
        return new SpecialData(computeTags(), computeRegistryInfos());
    }

    @Override
    public String toString() {
        return String.format("SpecialData{tags=%s, registries=%s}", tags, registries);
    }

    public static List<RegistryInfo> computeRegistryInfos() {
        return SpecialData
            .fetchRawRegistries()
            .values()
            .stream()
            .map(RegistryInfo::new)
            .collect(Collectors.toList());
    }

    private static Map<ResourceLocation, ForgeRegistry<? extends IForgeRegistryEntry<?>>> fetchRawRegistries() {
        Map<ResourceLocation, ForgeRegistry<? extends IForgeRegistryEntry<?>>> registries = new HashMap<>();
        try {
            Field f = RegistryManager.class.getDeclaredField("registries");
            f.setAccessible(true);

            Map<ResourceLocation, ForgeRegistry<? extends IForgeRegistryEntry<?>>> def = HashBiMap.create();

            registries.putAll(PUtil.castedGetField(f, RegistryManager.ACTIVE, def));
            registries.putAll(PUtil.castedGetField(f, RegistryManager.FROZEN, def));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return registries;
    }

    public static DummyBindingEvent computeBindingEvent() {
        final DummyBindingEvent bindingEvent = new DummyBindingEvent(
            ServerScriptManager.instance.scriptManager
        );
        KubeJSPlugins.forEachPlugin(plugin -> plugin.addBindings(bindingEvent));
        return bindingEvent;
    }

    public static Map<ResourceLocation, RecipeTypeJS> computeRecipeTypes() {
        final Map<ResourceLocation, RecipeTypeJS> typeMap = new HashMap<>();
        final RegisterRecipeHandlersEvent recipeEvent = new RegisterRecipeHandlersEvent(typeMap);

        KubeJSPlugins.forEachPlugin(plugin -> plugin.addRecipes(recipeEvent));
        return typeMap;
    }
}
