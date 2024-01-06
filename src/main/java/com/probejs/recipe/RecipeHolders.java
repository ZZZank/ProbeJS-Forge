package com.probejs.recipe;

import com.probejs.formatter.formatter.FormatterClass;
import com.probejs.formatter.formatter.FormatterNamespace;
import com.probejs.formatter.formatter.IFormatter;
import com.probejs.info.ClassInfo;
import dev.latvian.kubejs.recipe.RecipeTypeJS;
import dev.latvian.kubejs.recipe.RegisterRecipeHandlersEvent;
import dev.latvian.kubejs.util.KubeJSPlugins;
import dev.latvian.mods.rhino.ast.FunctionNode.Form;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.resources.ResourceLocation;

public class RecipeHolders {

    static FormatterNamespace namespaced;

    public static void init() {
        Map<ResourceLocation, RecipeTypeJS> recipeHandlers = new HashMap<>();
        RegisterRecipeHandlersEvent event = new RegisterRecipeHandlersEvent(recipeHandlers);
        KubeJSPlugins.forEachPlugin(plugin -> plugin.addRecipes(event));

        // RecipeTypeJS -> ClassInfo -> FormatterClass
        List<FormatterClass> formatterClasses = recipeHandlers
            .values()
            .stream()
            .map(value -> ClassInfo.getOrCache(value.factory.get().getClass()))
            .map(FormatterClass::new)
            .collect(Collectors.toList());

        namespaced = new FormatterNamespace("stub.probejs.recipeHolder", formatterClasses);
    }

    public static List<String> format(int indent, int stepIndent) {
        return namespaced.format(indent, stepIndent);
    }
}
