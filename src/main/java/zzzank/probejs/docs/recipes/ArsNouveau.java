package zzzank.probejs.docs.recipes;

import dev.latvian.kubejs.recipe.mod.ArsNouveauEnchantingApparatusRecipeJS;
import dev.latvian.kubejs.recipe.mod.ArsNouveauEnchantmentRecipeJS;
import dev.latvian.kubejs.recipe.mod.ArsNouveauGlyphPressRecipeJS;
import me.shedaniel.architectury.platform.Platform;
import net.minecraft.resources.ResourceLocation;
import zzzank.probejs.docs.Primitives;
import zzzank.probejs.lang.typescript.ScriptDump;
import zzzank.probejs.lang.typescript.code.type.BaseType;
import zzzank.probejs.lang.typescript.code.type.Types;
import zzzank.probejs.plugin.ProbeJSPlugin;

import java.util.Map;

import static zzzank.probejs.docs.recipes.BuiltinRecipeDocs.recipeFn;
import static zzzank.probejs.docs.recipes.KubeJS.INGR;
import static zzzank.probejs.docs.recipes.KubeJS.STACK;
import static zzzank.probejs.docs.recipes.Minecraft.INGR_N;

/**
 * @author ZZZank
 */
class ArsNouveau extends ProbeJSPlugin {

    @Override
    public void addPredefinedRecipeDoc(ScriptDump scriptDump, Map<ResourceLocation, BaseType> predefined) {
        if (!Platform.isModLoaded("ars_nouveau")) {
            return;
        }
        predefined.put(
            rl("enchanting_apparatus"),
            recipeFn()
                .param("output", STACK)
                .param("reagent", INGR)
                .param("inputs", INGR_N)
                .returnType(Types.type(ArsNouveauEnchantingApparatusRecipeJS.class))
                .build()
        );
        predefined.put(
            rl("enchantment"),
            recipeFn()
                .param("enchantment", Types.primitive("Special.Enchantment"))
                .param("level", Primitives.INTEGER)
                .param("inputs", INGR_N)
                .param("mana", Primitives.INTEGER)
                .returnType(Types.type(ArsNouveauEnchantmentRecipeJS.class))
                .build()
        );
        predefined.put(
            rl("glyph_recipe"),
            recipeFn()
                .param("output", STACK)
                .param("input", STACK)
                .param("tier", Types.STRING)
                .returnType(Types.type(ArsNouveauGlyphPressRecipeJS.class))
                .build()
        );
    }

    static ResourceLocation rl(String path) {
        return new ResourceLocation("ars_nouveau", path);
    }
}
