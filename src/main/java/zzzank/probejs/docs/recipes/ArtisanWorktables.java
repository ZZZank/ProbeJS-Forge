package zzzank.probejs.docs.recipes;

import dev.latvian.kubejs.recipe.mod.ShapedArtisanRecipeJS;
import dev.latvian.kubejs.recipe.mod.ShapelessArtisanRecipeJS;
import lombok.val;
import me.shedaniel.architectury.platform.Platform;
import zzzank.probejs.lang.typescript.ScriptDump;
import zzzank.probejs.lang.typescript.code.type.Types;

/**
 * @author ZZZank
 */
class ArtisanWorktables extends RecipeDocProvider {
    @Override
    public void addDocs(ScriptDump scriptDump) {
        val types = new String[]{
            "basic", "blacksmith", "carpenter", "chef", "chemist", "designer", "engineer", "farmer", "jeweler", "mage",
            "mason", "potter", "scribe", "tailor", "tanner"
        };
        val shapedReturn = Types.type(ShapedArtisanRecipeJS.class);
        val shapelessReturn = Types.type(ShapelessArtisanRecipeJS.class);
        for (val type : types) {
            add(type + "_shaped", BuiltinRecipeDocs.basicShapedRecipe(shapedReturn));
            add(type + "_shapeless", BuiltinRecipeDocs.basicShapelessRecipe(shapelessReturn));
        }
    }

    @Override
    public String getNamespace() {
        return "artisanworktables";
    }

    @Override
    public boolean shouldEnable() {
        return Platform.isModLoaded("artisanworktables");
    }
}
