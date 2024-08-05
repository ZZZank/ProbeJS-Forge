package zzzank.probejs.docs.recipes;

import zzzank.probejs.lang.typescript.ScriptDump;

/**
 * @author ZZZank
 */
class DankStorage extends RecipeDocProvider {
    @Override
    public void addDocs(ScriptDump scriptDump) {
        add("upgrade", RecipeDocUtil.basicShapedRecipe());
    }

    @Override
    public String namespace() {
        return "dankstorage";
    }
}
