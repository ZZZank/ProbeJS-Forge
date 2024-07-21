package zzzank.probejs.docs.recipes;

import dev.latvian.kubejs.fluid.FluidStackJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.recipe.special.ShapedKubeJSRecipe;
import dev.latvian.kubejs.recipe.special.ShapelessKubeJSRecipe;
import lombok.val;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import zzzank.probejs.lang.typescript.ScriptDump;
import zzzank.probejs.lang.typescript.code.type.TSClassType;
import zzzank.probejs.lang.typescript.code.type.Types;
import zzzank.probejs.lang.typescript.code.type.js.JSLambdaType;
import zzzank.probejs.plugin.ProbeJSPlugin;

import java.util.Map;

import static zzzank.probejs.lang.typescript.code.type.Types.lambda;

/**
 * @author ZZZank
 */
public class KubeJS extends ProbeJSPlugin {

    public static final TSClassType FLUID = Types.type(FluidStackJS.class);
    public static final TSClassType STACK = Types.type(ItemStackJS.class);
    public static final TSClassType INGR = Types.type(Ingredient.class);

    @Override
    public void addPredefinedRecipeDoc(ScriptDump scriptDump, Map<ResourceLocation, JSLambdaType> predefined) {
        val converter = scriptDump.transpiler.typeConverter;
        predefined.put(
            rl("shaped"),
            lambda()
                .param("output", STACK)
                .param("pattern", Minecraft.STR_N)
                .param("keys", Minecraft.STR2INGR)
                .returnType(converter.convertType(ShapedKubeJSRecipe.class))
                .build()
        );
        predefined.put(
            rl("shapeless"),
            Types.lambda().param("output", KubeJS.STACK)
                .param("inputs", Minecraft.INGR_N)
                .returnType(converter.convertType(ShapelessKubeJSRecipe.class))
                .build()
        );
    }

    public static ResourceLocation rl(String path) {
        return new ResourceLocation("kubejs", path);
    }
}
