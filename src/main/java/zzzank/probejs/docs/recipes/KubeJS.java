package zzzank.probejs.docs.recipes;

import dev.latvian.kubejs.recipe.special.ShapedKubeJSRecipe;
import dev.latvian.kubejs.recipe.special.ShapelessKubeJSRecipe;
import zzzank.probejs.lang.typescript.ScriptDump;
import zzzank.probejs.lang.typescript.code.type.Types;

import static zzzank.probejs.docs.recipes.BuiltinRecipeDocs.*;

/**
 * @author ZZZank
 */
class KubeJS extends RecipeDocProvider {

    @Override
    public void addDocs(ScriptDump scriptDump) {
        add("shaped", basicShapedRecipe(Types.type(ShapedKubeJSRecipe.class)));
        add("shapeless", basicShapelessRecipe(Types.type(ShapelessKubeJSRecipe.class)));
    }

    @Override
    public String namespace() {
        return "kubejs";
    }

    @Override
    public boolean shouldEnable() {
        return true;
    }
}
