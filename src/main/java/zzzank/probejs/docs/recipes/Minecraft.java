package zzzank.probejs.docs.recipes;

import dev.latvian.kubejs.recipe.minecraft.*;
import lombok.val;
import zzzank.probejs.lang.typescript.ScriptDump;

import static zzzank.probejs.docs.recipes.BuiltinRecipeDocs.basicCookingRecipe;
import static zzzank.probejs.docs.recipes.BuiltinRecipeDocs.INGR;
import static zzzank.probejs.docs.recipes.BuiltinRecipeDocs.STACK;

/**
 * @author ZZZank
 */
class Minecraft extends RecipeDocProvider {

    @Override
    public void addDocs(ScriptDump scriptDump) {
        val converter = scriptDump.transpiler.typeConverter;
        add("smelting", basicCookingRecipe());
        add("smoking", basicCookingRecipe());
        add("blasting", basicCookingRecipe());
        add("campfire_cooking", basicCookingRecipe());
        add("crafting_shaped", BuiltinRecipeDocs.basicShapedRecipe());
        add("crafting_shapeless", BuiltinRecipeDocs.basicShapelessRecipe());
        add(
            "stonecutting",
            recipeFn().param("output", STACK)
                .param("inputs", BuiltinRecipeDocs.INGR_N)
                .returnType(converter.convertType(StonecuttingRecipeJS.class))
                .build()
        );
        add(
            "smithing",
            recipeFn().param("output", STACK)
                .param("base", INGR)
                .param("addition", INGR)
                .returnType(converter.convertType(SmithingRecipeJS.class))
                .build()
        );
    }

    @Override
    public String namespace() {
        return "minecraft";
    }

    @Override
    public boolean shouldEnable() {
        return true;
    }
}
