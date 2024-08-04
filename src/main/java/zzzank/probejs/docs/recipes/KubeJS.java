package zzzank.probejs.docs.recipes;

import dev.latvian.kubejs.recipe.special.ShapedKubeJSRecipe;
import dev.latvian.kubejs.recipe.special.ShapelessKubeJSRecipe;
import lombok.val;
import net.minecraft.resources.ResourceLocation;
import zzzank.probejs.lang.typescript.ScriptDump;
import zzzank.probejs.lang.typescript.code.type.js.JSLambdaType;
import zzzank.probejs.plugin.ProbeJSPlugin;

import java.util.Map;

import static zzzank.probejs.docs.recipes.BuiltinRecipeDocs.*;

/**
 * @author ZZZank
 */
class KubeJS extends ProbeJSPlugin {

    @Override
    public void addPredefinedRecipeDoc(ScriptDump scriptDump, Map<ResourceLocation, JSLambdaType> predefined) {
        val converter = scriptDump.transpiler.typeConverter;
        predefined.put(
            rl("shaped"),
            basicShapedRecipe(converter.convertType(ShapedKubeJSRecipe.class))
        );
        predefined.put(
            rl("shapeless"),
            basicShapelessRecipe(converter.convertType(ShapelessKubeJSRecipe.class))
        );
    }

    public static ResourceLocation rl(String path) {
        return new ResourceLocation("kubejs", path);
    }
}
