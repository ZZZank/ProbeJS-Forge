package com.probejs.recipe;

import com.mojang.datafixers.util.Pair;
import com.probejs.ProbeJS;
import com.probejs.formatter.NameResolver;
import com.probejs.formatter.formatter.FormatterNamespace;
import com.probejs.util.PUtil;
import dev.latvian.kubejs.recipe.RecipeTypeJS;
import dev.latvian.kubejs.recipe.RegisterRecipeHandlersEvent;
import dev.latvian.kubejs.util.KubeJSPlugins;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.resources.ResourceLocation;

public class RecipeHolders {

    static FormatterNamespace namespaced;
    static Map<String, List<Pair<String, String>>> namespacedMap = new HashMap<>();

    public static void init() {
        Map<ResourceLocation, RecipeTypeJS> recipeHandlers = new HashMap<>();
        RegisterRecipeHandlersEvent event = new RegisterRecipeHandlersEvent(recipeHandlers);
        KubeJSPlugins.forEachPlugin(plugin -> plugin.addRecipes(event));

        //TODO: remove when out of dev
        int threshold = 10;
        for (Entry<ResourceLocation, RecipeTypeJS> entry : recipeHandlers.entrySet()) {
            if (threshold-- < 0) {
                break;
            }
            ProbeJS.LOGGER.info(entry.getKey());
            // ProbeJS.LOGGER.info(entry.getValue()); // same as getKey()
            Class<?> clazz = entry.getValue().getClass();
            ProbeJS.LOGGER.info(clazz);

            NameResolver.getResolvedName(clazz.getName()).getLastName();
        }

        recipeHandlers.forEach((key, value) -> {
            String namespace = key.getNamespace();
            String path = key.getPath();
            String invokeName =
                "Internal." + NameResolver.getResolvedName(value.factory.get().getClass().getName()).getLastName();
            
            namespacedMap
                .computeIfAbsent(namespace, k -> new ArrayList<Pair<String, String>>())
                .add(new Pair<>(path, invokeName));
        });
    }

    public static List<String> format(int indent, int stepIndent) {
        List<String> formatted = new ArrayList<>();
        // head
        formatted.add("declare namespace stub.probejs.recipeHolder {");
        // each class
        for (Entry<String, List<Pair<String, String>>> entry : namespacedMap.entrySet()) {
            formatted.add(String.format("    class %s {", entry.getKey()));
            // each method inside class
            for (Pair<String, String> pair : entry.getValue()) {
                // we dont know how a Recipe Serializer actually works, so only `...args`
                formatted.add(PUtil.indent(8) + pair.getFirst() + "(...args: object): " + pair.getSecond());
            }
            formatted.add("    }");
        }
        // end
        formatted.add("}");
        return formatted;
    }
}
