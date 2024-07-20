package zzzank.probejs.docs.recipes;

import dev.latvian.kubejs.recipe.minecraft.*;
import lombok.val;
import net.minecraft.resources.ResourceLocation;
import zzzank.probejs.docs.Primitives;
import zzzank.probejs.lang.typescript.ScriptDump;
import zzzank.probejs.lang.typescript.code.type.BaseType;
import zzzank.probejs.lang.typescript.code.type.TSArrayType;
import zzzank.probejs.lang.typescript.code.type.Types;
import zzzank.probejs.lang.typescript.code.type.js.JSLambdaType;
import zzzank.probejs.lang.typescript.code.type.js.JSObjectType;
import zzzank.probejs.plugin.ProbeJSPlugin;

import java.util.Map;

/**
 * @author ZZZank
 */
public class Minecraft extends ProbeJSPlugin {

    public static final TSArrayType INGR_N = Types.array(KubeJS.INGR);
    public static final TSArrayType STR_N = Types.array(Primitives.CHAR_SEQUENCE);
    public static final JSObjectType STR2INGR = Types.object().member("[x in string]", KubeJS.INGR).build();

    public static JSLambdaType simpleIngrToStackRecipe(BaseType returnType) {
        return Types.lambda().param("output", KubeJS.STACK)
            .param("input", KubeJS.INGR)
            .returnType(returnType)
            .build();
    }

    @Override
    public void addPredefinedRecipeDoc(ScriptDump scriptDump, Map<ResourceLocation, JSLambdaType> predefined) {
        val converter = scriptDump.transpiler.typeConverter;
        predefined.put(
            rl("smelting"),
            Types.lambda().param("output", KubeJS.STACK)
                .param("input", KubeJS.INGR)
                .returnType(converter.convertType(CookingRecipeJS.class))
                .build()
        );
        predefined.put(
            rl("smoking"),
            Types.lambda().param("output", KubeJS.STACK)
                .param("input", KubeJS.INGR)
                .returnType(converter.convertType(CookingRecipeJS.class))
                .build()
        );
        predefined.put(
            rl("blasting"),
            Types.lambda().param("output", KubeJS.STACK)
                .param("input", KubeJS.INGR)
                .returnType(converter.convertType(CookingRecipeJS.class))
                .build()
        );
        predefined.put(
            rl("campfire_cooking"),
            Types.lambda().param("output", KubeJS.STACK)
                .param("input", KubeJS.INGR)
                .returnType(converter.convertType(CookingRecipeJS.class))
                .build()
        );
        predefined.put(
            rl("crafting_shaped"),
            Types.lambda().param("output", KubeJS.STACK)
                .param("pattern", STR_N)
                .param("items", STR2INGR)
                .returnType(converter.convertType(ShapedRecipeJS.class))
                .build()
        );
        predefined.put(
            rl("crafting_shapeless"),
            Types.lambda().param("output", KubeJS.STACK)
                .param("inputs", INGR_N)
                .returnType(converter.convertType(ShapelessRecipeJS.class))
                .build()
        );
        predefined.put(
            rl("stonecutting"),
            Types.lambda().param("output", KubeJS.STACK)
                .param("inputs", INGR_N)
                .returnType(converter.convertType(StonecuttingRecipeJS.class))
                .build()
        );
        predefined.put(
            rl("smithing"),
            Types.lambda().param("output", KubeJS.STACK)
                .param("base", KubeJS.INGR)
                .param("addition", KubeJS.INGR)
                .returnType(converter.convertType(SmithingRecipeJS.class))
                .build()
        );
    }

    private static ResourceLocation rl(String path) {
        return new ResourceLocation("minecraft", path);
    }
}
