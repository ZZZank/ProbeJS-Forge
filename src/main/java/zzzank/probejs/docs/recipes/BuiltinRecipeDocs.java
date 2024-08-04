package zzzank.probejs.docs.recipes;

import dev.latvian.kubejs.fluid.FluidStackJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.recipe.minecraft.CookingRecipeJS;
import dev.latvian.kubejs.recipe.minecraft.ShapedRecipeJS;
import dev.latvian.kubejs.recipe.minecraft.ShapelessRecipeJS;
import lombok.val;
import net.minecraft.resources.ResourceLocation;
import zzzank.probejs.docs.Primitives;
import zzzank.probejs.lang.typescript.ScriptDump;
import zzzank.probejs.lang.typescript.code.type.BaseType;
import zzzank.probejs.lang.typescript.code.type.TSArrayType;
import zzzank.probejs.lang.typescript.code.type.TSClassType;
import zzzank.probejs.lang.typescript.code.type.Types;
import zzzank.probejs.lang.typescript.code.type.js.JSLambdaType;
import zzzank.probejs.lang.typescript.code.type.js.JSObjectType;
import zzzank.probejs.plugin.ProbeJSPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

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
    public static final TSClassType FLUID = Types.type(FluidStackJS.class);
    public static final TSClassType STACK = Types.type(ItemStackJS.class);
    public static final TSClassType INGR = Types.type(IngredientJS.class);
    public static final JSObjectType STR2INGR = Types.object().indexParam(INGR).build();
    public static final TSArrayType STACK_N = STACK.asArray();
    public static final TSArrayType INGR_N = Types.array(INGR);
    public static final TSArrayType STR_N = Types.array(Primitives.CHAR_SEQUENCE);

    public static JSLambdaType.Builder recipeFn() {
        return Types.lambda().methodStyle();
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

    public static JSLambdaType basicCookingRecipe() {
        return basicCookingRecipe(Types.type(CookingRecipeJS.class));
    }

    public static BaseType selfOrArray(BaseType type) {
        return Types.or(type, Types.array(type));
    }

    @Override
    public void addPredefinedRecipeDoc(ScriptDump scriptDump, Map<ResourceLocation, JSLambdaType> predefined) {
        for (Supplier<ProbeJSPlugin> supplier : ALL) {
            supplier.get().addPredefinedRecipeDoc(scriptDump, predefined);
        }
    }
}
