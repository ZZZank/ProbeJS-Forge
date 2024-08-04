package zzzank.probejs.docs.recipes;

import lombok.val;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModList;
import zzzank.probejs.lang.typescript.ScriptDump;
import zzzank.probejs.lang.typescript.code.type.BaseType;
import zzzank.probejs.lang.typescript.code.type.Types;
import zzzank.probejs.lang.typescript.code.type.js.JSLambdaType;
import zzzank.probejs.plugin.ProbeJSPlugin;

import java.util.Map;

import static zzzank.probejs.docs.recipes.BuiltinRecipeDocs.*;

/**
 * @author ZZZank
 */
class Thermal extends ProbeJSPlugin {

    public static final BaseType MIXED_IN = Types.or(INGR, FLUID);
    public static final BaseType MIXED_OUT = Types.or(STACK, FLUID);

    public static JSLambdaType catalystStyleRecipe() {
        return recipeFn()
            .param("input", INGR)
            .returnType(classType("dev.latvian.kubejs.thermal.CatalystRecipeJS"))
            .build();
    }

    public static JSLambdaType fuelStyleRecipe() {
        return recipeFn()
            .param("input", INGR)
            .returnType(classType("dev.latvian.kubejs.thermal.FuelRecipeJS"))
            .build();
    }

    private static ResourceLocation rl(String path) {
        return new ResourceLocation("thermal", path);
    }

    @Override
    public void addPredefinedRecipeDoc(ScriptDump scriptDump, Map<ResourceLocation, JSLambdaType> predefined) {
        if (!ModList.get().isLoaded("thermal") || !ModList.get().isLoaded("kubejs_thermal")) {
            return;
        }
        val converter = scriptDump.transpiler.typeConverter;
        //fuel
        predefined.put(rl("compression_fuel"), fuelStyleRecipe());
        predefined.put(rl("lapidary_fuel"), fuelStyleRecipe());
        predefined.put(rl("magmatic_fuel"), fuelStyleRecipe());
        predefined.put(rl("numismatic_fuel"), fuelStyleRecipe());
        predefined.put(rl("stirling_fuel"), fuelStyleRecipe());
        //catalyst
        predefined.put(rl("insolator_catalyst"), catalystStyleRecipe());
        predefined.put(rl("pulverizer_catalyst"), catalystStyleRecipe());
        predefined.put(rl("smelter_catalyst"), catalystStyleRecipe());
        //general
        val basicReturn = classType("dev.latvian.kubejs.thermal.BasicThermalRecipeJS");
        predefined.put(
            rl("bottler"),
            recipeFn().param("output", STACK)
                .param("input", selfOrArray(MIXED_IN))
                .returnType(basicReturn)
                .build()
        );
        predefined.put(
            rl("brewer"),
            recipeFn().param("output", FLUID)
                .param("input", selfOrArray(MIXED_IN))
                .returnType(basicReturn)
                .build()
        );
        predefined.put(
            rl("centrifuge"),
            recipeFn().param("output", selfOrArray(MIXED_OUT))
                .param("input", INGR)
                .returnType(basicReturn)
                .build()
        );
        predefined.put(
            rl("crucible"),
            recipeFn().param("output", FLUID).param("input", INGR).returnType(basicReturn).build()
        );
        predefined.put(
            rl("furnance"),
            recipeFn().param("output", STACK).param("input", INGR).returnType(basicReturn).build()
        );
        predefined.put(
            rl("insolator"),
            recipeFn().param("output", selfOrArray(STACK))
                .param("input", INGR)
                .returnType(basicReturn)
                .build()
        );
        predefined.put(
            rl("press"),
            recipeFn().param("outputs", selfOrArray(MIXED_OUT))
                .param("input", selfOrArray(INGR))
                .returnType(basicReturn)
                .build()
        );
        predefined.put(
            rl("pulverizer"),
            recipeFn().param("output", selfOrArray(STACK))
                .param("input", INGR)
                .returnType(basicReturn)
                .build()
        );
        predefined.put(
            rl("pyrolyzer"),
            recipeFn().param("outputs", selfOrArray(MIXED_OUT))
                .param("input", INGR)
                .returnType(basicReturn)
                .build()
        );
        predefined.put(
            rl("refinery"),
            recipeFn().param("outputs", selfOrArray(MIXED_OUT))
                .param("input", FLUID)
                .returnType(basicReturn)
                .build()
        );
        predefined.put(
            rl("sawmill"),
            recipeFn().param("outputs", selfOrArray(MIXED_OUT))
                .param("input", INGR)
                .returnType(basicReturn)
                .build()
        );
        predefined.put(
            rl("smelter"),
            recipeFn().param("outputs", selfOrArray(STACK))
                .param("inputs", selfOrArray(INGR))
                .returnType(basicReturn)
                .build()
        );
    }
}
