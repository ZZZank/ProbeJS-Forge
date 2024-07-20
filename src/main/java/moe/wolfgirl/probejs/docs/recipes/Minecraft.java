package moe.wolfgirl.probejs.docs.recipes;

import dev.latvian.kubejs.recipe.minecraft.*;
import lombok.val;
import moe.wolfgirl.probejs.docs.Primitives;
import moe.wolfgirl.probejs.lang.typescript.ScriptDump;
import moe.wolfgirl.probejs.lang.typescript.code.type.BaseType;
import moe.wolfgirl.probejs.lang.typescript.code.type.TSArrayType;
import moe.wolfgirl.probejs.lang.typescript.code.type.Types;
import moe.wolfgirl.probejs.lang.typescript.code.type.js.JSLambdaType;
import moe.wolfgirl.probejs.lang.typescript.code.type.js.JSObjectType;
import moe.wolfgirl.probejs.plugin.ProbeJSPlugin;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

import static moe.wolfgirl.probejs.docs.recipes.KubeJS.INGR;
import static moe.wolfgirl.probejs.docs.recipes.KubeJS.STACK;
import static moe.wolfgirl.probejs.lang.typescript.code.type.Types.lambda;

/**
 * @author ZZZank
 */
public class Minecraft extends ProbeJSPlugin {

    public static final TSArrayType INGR_N = Types.array(INGR);
    public static final TSArrayType STR_N = Types.array(Primitives.CHAR_SEQUENCE);
    public static final JSObjectType STR2INGR = Types.object().member("[x in string]", INGR).build();

    public static JSLambdaType simpleIngrToStackRecipe(BaseType returnType) {
        return lambda().param("output", STACK)
            .param("input", INGR)
            .returnType(returnType)
            .build();
    }

    @Override
    public void addPredefinedRecipeDoc(ScriptDump scriptDump, Map<ResourceLocation, JSLambdaType> predefined) {
        val converter = scriptDump.transpiler.typeConverter;
        predefined.put(
            rl("smelting"),
            lambda().param("output", STACK)
                .param("input", INGR)
                .returnType(converter.convertType(CookingRecipeJS.class))
                .build()
        );
        predefined.put(
            rl("smoking"),
            lambda().param("output", STACK)
                .param("input", INGR)
                .returnType(converter.convertType(CookingRecipeJS.class))
                .build()
        );
        predefined.put(
            rl("blasting"),
            lambda().param("output", STACK)
                .param("input", INGR)
                .returnType(converter.convertType(CookingRecipeJS.class))
                .build()
        );
        predefined.put(
            rl("campfire_cooking"),
            lambda().param("output", STACK)
                .param("input", INGR)
                .returnType(converter.convertType(CookingRecipeJS.class))
                .build()
        );
        predefined.put(
            rl("crafting_shaped"),
            lambda().param("output", STACK)
                .param("pattern", STR_N)
                .param("items", STR2INGR)
                .returnType(converter.convertType(ShapedRecipeJS.class))
                .build()
        );
        predefined.put(
            rl("crafting_shapeless"),
            lambda().param("output", STACK)
                .param("inputs", INGR_N)
                .returnType(converter.convertType(ShapelessRecipeJS.class))
                .build()
        );
        predefined.put(
            rl("stonecutting"),
            lambda().param("output", STACK)
                .param("inputs", INGR_N)
                .returnType(converter.convertType(StonecuttingRecipeJS.class))
                .build()
        );
        predefined.put(
            rl("smithing"),
            lambda().param("output", STACK)
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
