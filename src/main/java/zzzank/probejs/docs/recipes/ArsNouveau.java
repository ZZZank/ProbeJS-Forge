package zzzank.probejs.docs.recipes;

import dev.latvian.kubejs.recipe.mod.ArsNouveauEnchantingApparatusRecipeJS;
import dev.latvian.kubejs.recipe.mod.ArsNouveauEnchantmentRecipeJS;
import dev.latvian.kubejs.recipe.mod.ArsNouveauGlyphPressRecipeJS;
import me.shedaniel.architectury.platform.Platform;
import net.minecraft.resources.ResourceLocation;
import zzzank.probejs.docs.Primitives;
import zzzank.probejs.lang.typescript.ScriptDump;
import zzzank.probejs.lang.typescript.code.type.Types;
import zzzank.probejs.lang.typescript.code.type.js.JSLambdaType;
import zzzank.probejs.plugin.ProbeJSPlugin;

import java.util.Map;

import static zzzank.probejs.docs.recipes.BuiltinRecipeDocs.recipeFn;
import static zzzank.probejs.docs.recipes.KubeJS.INGR;
import static zzzank.probejs.docs.recipes.KubeJS.STACK;
import static zzzank.probejs.docs.recipes.Minecraft.INGR_N;

/**
 * @author ZZZank
 */
class ArsNouveau extends RecipeDocProvider {

    @Override
    public void addDocs(ScriptDump scriptDump) {
        add(
            "enchanting_apparatus",
            recipeFn()
                .param("output", STACK)
                .param("reagent", INGR)
                .param("inputs", INGR_N)
                .returnType(Types.type(ArsNouveauEnchantingApparatusRecipeJS.class))
                .build()
        );
        add(
            "enchantment",
            recipeFn()
                .param("enchantment", Types.primitive("Special.Enchantment"))
                .param("level", Primitives.INTEGER)
                .param("inputs", INGR_N)
                .param("mana", Primitives.INTEGER)
                .returnType(Types.type(ArsNouveauEnchantmentRecipeJS.class))
                .build()
        );
        add(
            "glyph_recipe",
            recipeFn()
                .param("output", STACK)
                .param("input", STACK)
                .param("tier", Types.STRING)
                .returnType(Types.type(ArsNouveauGlyphPressRecipeJS.class))
                .build()
        );
    }

    @Override
    public String namespace() {
        return "ars_nouveau";
    }
}
