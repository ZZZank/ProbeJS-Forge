package zzzank.probejs.docs.recipes;

import zzzank.probejs.lang.typescript.ScriptDump;

import static zzzank.probejs.docs.recipes.BuiltinRecipeDocs.basicShapedRecipe;

/**
 * @author ZZZank
 */
class Cucumber extends RecipeDocProvider {
    @Override
    public void addDocs(ScriptDump scriptDump) {
        add("shaped_no_mirror", basicShapedRecipe());
    }

    @Override
    public String namespace() {
        return "cucumber";
    }
}
