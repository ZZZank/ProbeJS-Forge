package moe.wolfgirl.probejs.docs.recipes;

import lombok.val;
import moe.wolfgirl.probejs.lang.typescript.ScriptDump;
import moe.wolfgirl.probejs.lang.typescript.code.type.BaseType;
import moe.wolfgirl.probejs.lang.typescript.code.type.TSClassType;
import moe.wolfgirl.probejs.lang.typescript.code.type.Types;
import moe.wolfgirl.probejs.lang.typescript.code.type.js.JSLambdaType;
import moe.wolfgirl.probejs.plugin.ProbeJSPlugin;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModList;

import java.util.Map;

import static moe.wolfgirl.probejs.docs.recipes.KubeJS.*;
import static moe.wolfgirl.probejs.lang.typescript.code.type.Types.lambda;

/**
 * @author ZZZank
 */
public class Thermal extends ProbeJSPlugin {

    public static final BaseType MIXED_IN = Types.or(INGR, FLUID);
    public static final BaseType MIXED_OUT = Types.or(STACK, FLUID);

    public static JSLambdaType catalystStyleRecipe() {
        return lambda()
            .param("input", INGR)
            .returnType(classType("dev.latvian.kubejs.thermal.CatalystRecipeJS"))
            .build();
    }

    public static JSLambdaType fuelStyleRecipe() {
        return lambda()
            .param("input", INGR)
            .returnType(classType("dev.latvian.kubejs.thermal.FuelRecipeJS"))
            .build();
    }

    public static TSClassType classType(String className) {
        try {
            val c = Class.forName(className);
            return Types.type(c);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static ResourceLocation rl(String path) {
        return new ResourceLocation("thermal", path);
    }

    public static BaseType selfOrArray(BaseType type) {
        return Types.or(type, Types.array(type));
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
            lambda().param("output", STACK)
                .param("input", selfOrArray(MIXED_IN))
                .returnType(basicReturn)
                .build()
        );
        predefined.put(
            rl("brewer"),
            lambda().param("output", FLUID)
                .param("input", selfOrArray(MIXED_IN))
                .returnType(basicReturn)
                .build()
        );
        predefined.put(
            rl("centrifuge"),
            lambda().param("output", selfOrArray(MIXED_OUT))
                .param("input", INGR)
                .returnType(basicReturn)
                .build()
        );
        predefined.put(
            rl("crucible"),
            lambda().param("output", FLUID).param("input", INGR).returnType(basicReturn).build()
        );
        predefined.put(
            rl("furnance"),
            lambda().param("output", STACK).param("input", INGR).returnType(basicReturn).build()
        );
        predefined.put(
            rl("insolator"),
            lambda().param("output", selfOrArray(STACK))
                .param("input", INGR)
                .returnType(basicReturn)
                .build()
        );
        predefined.put(
            rl("press"),
            lambda().param("outputs", selfOrArray(MIXED_OUT))
                .param("input", selfOrArray(INGR))
                .returnType(basicReturn)
                .build()
        );
        predefined.put(
            rl("pulverizer"),
            lambda().param("output", selfOrArray(STACK))
                .param("input", INGR)
                .returnType(basicReturn)
                .build()
        );
        predefined.put(
            rl("pyrolyzer"),
            lambda().param("outputs", selfOrArray(MIXED_OUT))
                .param("input", INGR)
                .returnType(basicReturn)
                .build()
        );
        predefined.put(
            rl("refinery"),
            lambda().param("outputs", selfOrArray(MIXED_OUT))
                .param("input", FLUID)
                .returnType(basicReturn)
                .build()
        );
        predefined.put(
            rl("sawmill"),
            lambda().param("outputs", selfOrArray(MIXED_OUT))
                .param("input", INGR)
                .returnType(basicReturn)
                .build()
        );
        predefined.put(
            rl("smelter"),
            lambda().param("outputs", selfOrArray(STACK))
                .param("inputs", selfOrArray(INGR))
                .returnType(basicReturn)
                .build()
        );
    }
}
