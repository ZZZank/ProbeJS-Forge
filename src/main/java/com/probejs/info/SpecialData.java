package com.probejs.info;

import com.github.bsideup.jabel.Desugar;
import com.google.common.collect.HashBiMap;
import com.probejs.capture.DummyBindingEvent;
import com.probejs.util.PUtil;
import dev.latvian.kubejs.recipe.RecipeTypeJS;
import dev.latvian.kubejs.recipe.RegisterRecipeHandlersEvent;
import dev.latvian.kubejs.server.ServerScriptManager;
import dev.latvian.kubejs.util.KubeJSPlugins;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.val;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryManager;

@Desugar
public record SpecialData(Map<ResourceLocation, Collection<ResourceLocation>> tags,
                          Collection<RegistryInfo> registries) {

    private static SpecialData INSTANCE;

    public static SpecialData instance() {
        return INSTANCE;
    }

    public static void refresh() {
        val rInfos = SpecialData
            .fetchRawRegistries()
            .values()
            .stream()
            .map(RegistryInfo::new)
            .collect(Collectors.toList());
        SpecialData.INSTANCE = new SpecialData(extractTagsFrom(rInfos), rInfos);
    }

    private static Map<ResourceLocation, Collection<ResourceLocation>> extractTagsFrom(List<RegistryInfo> registries) {
        val tags = new HashMap<ResourceLocation, Collection<ResourceLocation>>();
        for (val rInfo : registries) {
            val tagHelper = rInfo.tagHelper();
            if (tagHelper == null) {
                continue;
            }
            val names = tagHelper.getAllTags().getAvailableTags();
            val id = rInfo.id();
            tags.put(id, names);
        }
        return tags;
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
        val bindingEvent = new DummyBindingEvent(ServerScriptManager.instance.scriptManager);
        KubeJSPlugins.forEachPlugin(plugin -> plugin.addBindings(bindingEvent));
        return bindingEvent;
    }

    public static Map<ResourceLocation, RecipeTypeJS> computeRecipeTypes() {
        val typeMap = new HashMap<ResourceLocation, RecipeTypeJS>();
        val recipeEvent = new RegisterRecipeHandlersEvent(typeMap);

        KubeJSPlugins.forEachPlugin(plugin -> plugin.addRecipes(recipeEvent));
        return typeMap;
    }
}
