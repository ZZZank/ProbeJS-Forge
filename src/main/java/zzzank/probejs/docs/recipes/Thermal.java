package zzzank.probejs.docs.recipes;

import lombok.val;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModList;
import zzzank.probejs.lang.typescript.ScriptDump;
import zzzank.probejs.lang.typescript.code.type.BaseType;
import zzzank.probejs.lang.typescript.code.type.TSClassType;
import zzzank.probejs.lang.typescript.code.type.Types;
import zzzank.probejs.lang.typescript.code.type.js.JSLambdaType;
import zzzank.probejs.plugin.ProbeJSPlugin;

import java.util.Map;

/**
 * @author ZZZank
 */
class Thermal extends ProbeJSPlugin {

    public static final BaseType MIXED_IN = Types.or(KubeJS.INGR, KubeJS.FLUID);
    public static final BaseType MIXED_OUT = Types.or(KubeJS.STACK, KubeJS.FLUID);

    public static JSLambdaType catalystStyleRecipe() {
        return Types.lambda()
            .param("input", KubeJS.INGR)
            .returnType(classType("dev.latvian.kubejs.thermal.CatalystRecipeJS"))
            .build();
    }

    public static JSLambdaType fuelStyleRecipe() {
        return Types.lambda()
            .param("input", KubeJS.INGR)
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
            Types.lambda().param("output", KubeJS.STACK)
                .param("input", selfOrArray(MIXED_IN))
                .returnType(basicReturn)
                .build()
        );
        predefined.put(
            rl("brewer"),
            Types.lambda().param("output", KubeJS.FLUID)
                .param("input", selfOrArray(MIXED_IN))
                .returnType(basicReturn)
                .build()
        );
        predefined.put(
            rl("centrifuge"),
            Types.lambda().param("output", selfOrArray(MIXED_OUT))
                .param("input", KubeJS.INGR)
                .returnType(basicReturn)
                .build()
        );
        predefined.put(
            rl("crucible"),
            Types.lambda().param("output", KubeJS.FLUID).param("input", KubeJS.INGR).returnType(basicReturn).build()
        );
        predefined.put(
            rl("furnance"),
            Types.lambda().param("output", KubeJS.STACK).param("input", KubeJS.INGR).returnType(basicReturn).build()
        );
        predefined.put(
            rl("insolator"),
            Types.lambda().param("output", selfOrArray(KubeJS.STACK))
                .param("input", KubeJS.INGR)
                .returnType(basicReturn)
                .build()
        );
        predefined.put(
            rl("press"),
            Types.lambda().param("outputs", selfOrArray(MIXED_OUT))
                .param("input", selfOrArray(KubeJS.INGR))
                .returnType(basicReturn)
                .build()
        );
        predefined.put(
            rl("pulverizer"),
            Types.lambda().param("output", selfOrArray(KubeJS.STACK))
                .param("input", KubeJS.INGR)
                .returnType(basicReturn)
                .build()
        );
        predefined.put(
            rl("pyrolyzer"),
            Types.lambda().param("outputs", selfOrArray(MIXED_OUT))
                .param("input", KubeJS.INGR)
                .returnType(basicReturn)
                .build()
        );
        predefined.put(
            rl("refinery"),
            Types.lambda().param("outputs", selfOrArray(MIXED_OUT))
                .param("input", KubeJS.FLUID)
                .returnType(basicReturn)
                .build()
        );
        predefined.put(
            rl("sawmill"),
            Types.lambda().param("outputs", selfOrArray(MIXED_OUT))
                .param("input", KubeJS.INGR)
                .returnType(basicReturn)
                .build()
        );
        predefined.put(
            rl("smelter"),
            Types.lambda().param("outputs", selfOrArray(KubeJS.STACK))
                .param("inputs", selfOrArray(KubeJS.INGR))
                .returnType(basicReturn)
                .build()
        );
    }
}
