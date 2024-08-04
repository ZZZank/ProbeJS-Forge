package zzzank.probejs.docs.recipes;

import dev.latvian.kubejs.recipe.minecraft.*;
import lombok.val;
import net.minecraft.resources.ResourceLocation;
import zzzank.probejs.lang.typescript.ScriptDump;
import zzzank.probejs.lang.typescript.code.type.js.JSLambdaType;
import zzzank.probejs.plugin.ProbeJSPlugin;

import java.util.Map;

import static zzzank.probejs.docs.recipes.BuiltinRecipeDocs.basicCookingRecipe;
import static zzzank.probejs.docs.recipes.BuiltinRecipeDocs.recipeFn;
import static zzzank.probejs.docs.recipes.BuiltinRecipeDocs.INGR;
import static zzzank.probejs.docs.recipes.BuiltinRecipeDocs.STACK;

/**
 * @author ZZZank
 */
class Minecraft extends ProbeJSPlugin {

    @Override
    public void addPredefinedRecipeDoc(ScriptDump scriptDump, Map<ResourceLocation, JSLambdaType> predefined) {
        val converter = scriptDump.transpiler.typeConverter;
        predefined.put(rl("smelting"), basicCookingRecipe());
        predefined.put(rl("smoking"), basicCookingRecipe());
        predefined.put(rl("blasting"), basicCookingRecipe());
        predefined.put(rl("campfire_cooking"), basicCookingRecipe());
        predefined.put(rl("crafting_shaped"), BuiltinRecipeDocs.basicShapedRecipe());
        predefined.put(rl("crafting_shapeless"), BuiltinRecipeDocs.basicShapelessRecipe());
        predefined.put(
            rl("stonecutting"),
            recipeFn().param("output", STACK)
                .param("inputs", BuiltinRecipeDocs.INGR_N)
                .returnType(converter.convertType(StonecuttingRecipeJS.class))
                .build()
        );
        predefined.put(
            rl("smithing"),
            recipeFn().param("output", STACK)
                .param("base", INGR)
                .param("addition", INGR)
                .returnType(converter.convertType(SmithingRecipeJS.class))
                .build()
        );
    }

    private static ResourceLocation rl(String path) {
        return new ResourceLocation("minecraft", path);
    }
}
