package zzzank.probejs.docs.recipes;

import dev.latvian.kubejs.recipe.mod.ArsNouveauEnchantingApparatusRecipeJS;
import dev.latvian.kubejs.recipe.mod.ArsNouveauEnchantmentRecipeJS;
import dev.latvian.kubejs.recipe.mod.ArsNouveauGlyphPressRecipeJS;
import zzzank.probejs.docs.Primitives;
import zzzank.probejs.lang.typescript.ScriptDump;
import zzzank.probejs.lang.typescript.code.type.Types;

import static zzzank.probejs.docs.recipes.RecipeDocUtil.INGR;
import static zzzank.probejs.docs.recipes.RecipeDocUtil.STACK;
import static zzzank.probejs.docs.recipes.RecipeDocUtil.INGR_N;

/**
 * @author ZZZank
 */
class ArsNouveau extends RecipeDocProvider {

    @Override
    public void addDocs(ScriptDump scriptDump) {
        add(
            "enchanting_apparatus",
            recipeFn()
                .output(STACK)
                .param("reagent", INGR)
                .inputs(INGR_N)
                .returnType(Types.type(ArsNouveauEnchantingApparatusRecipeJS.class))
        );
        add(
            "enchantment",
            recipeFn()
                .param("enchantment", Types.primitive("Special.Enchantment"))
                .param("level", Primitives.INTEGER)
                .inputs(INGR_N)
                .param("mana", Primitives.INTEGER)
                .returnType(Types.type(ArsNouveauEnchantmentRecipeJS.class))
        );
        add(
            "glyph_recipe",
            recipeFn()
                .output(STACK)
                .input(STACK)
                .param("tier", Types.STRING)
                .returnType(Types.type(ArsNouveauGlyphPressRecipeJS.class))
        );
    }

    @Override
    public String namespace() {
        return "ars_nouveau";
    }
}
