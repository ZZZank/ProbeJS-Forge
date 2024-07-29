package zzzank.probejs.docs.recipes;

import dev.latvian.kubejs.recipe.minecraft.CookingRecipeJS;
import dev.latvian.kubejs.recipe.minecraft.ShapedRecipeJS;
import dev.latvian.kubejs.recipe.minecraft.ShapelessRecipeJS;
import lombok.val;
import net.minecraft.resources.ResourceLocation;
import zzzank.probejs.lang.transpiler.TypeConverter;
import zzzank.probejs.lang.typescript.ScriptDump;
import zzzank.probejs.lang.typescript.code.type.BaseType;
import zzzank.probejs.lang.typescript.code.type.TSClassType;
import zzzank.probejs.lang.typescript.code.type.Types;
import zzzank.probejs.lang.typescript.code.type.js.JSLambdaType;
import zzzank.probejs.plugin.ProbeJSPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static zzzank.probejs.docs.recipes.KubeJS.INGR;
import static zzzank.probejs.docs.recipes.KubeJS.STACK;
import static zzzank.probejs.docs.recipes.Minecraft.*;

/**
 * @author ZZZank
 */
public class BuiltinRecipeDocs extends ProbeJSPlugin {

    public static final List<Supplier<ProbeJSPlugin>> ALL = new ArrayList<>(Arrays.asList(
        Minecraft::new,
        ArtisanWorktables::new,
        Botania::new,
        ArsNouveau::new,
        Thermal::new,
        KubeJS::new
    ));

    public static JSLambdaType.Builder recipeFn() {
        return Types.lambda().method();
    }

    public static TSClassType classType(String className) {
        try {
            val c = Class.forName(className);
            return Types.type(c);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static JSLambdaType basicShapedRecipe() {
        return basicShapedRecipe(Types.type(ShapedRecipeJS.class));
    }

    public static JSLambdaType basicShapedRecipe(BaseType returnType) {
        return recipeFn()
            .param("output", STACK)
            .param("pattern", STR_N)
            .param("items", STR2INGR)
            .returnType(returnType)
            .build();
    }

    public static JSLambdaType basicShapelessRecipe() {
        return basicShapelessRecipe(Types.type(ShapelessRecipeJS.class));
    }

    public static JSLambdaType basicShapelessRecipe(BaseType returnType) {
        return recipeFn()
            .param("output", STACK)
            .param("inputs", INGR_N)
            .returnType(returnType)
            .build();
    }

    public static JSLambdaType basicCookingRecipe(BaseType returnType) {
        return recipeFn()
            .param("output", STACK)
            .param("input", INGR)
            .returnType(returnType)
            .build();
    }

    public static JSLambdaType basicCookingRecipe(TypeConverter converter) {
        return basicCookingRecipe(converter.convertType(CookingRecipeJS.class));
    }

    @Override
    public void addPredefinedRecipeDoc(ScriptDump scriptDump, Map<ResourceLocation, JSLambdaType> predefined) {
        for (Supplier<ProbeJSPlugin> supplier : ALL) {
            supplier.get().addPredefinedRecipeDoc(scriptDump, predefined);
        }
    }
}
