package zzzank.probejs.docs.recipes;

import lombok.val;
import me.shedaniel.architectury.platform.Platform;
import zzzank.probejs.lang.typescript.ScriptDump;
import zzzank.probejs.lang.typescript.code.type.BaseType;
import zzzank.probejs.lang.typescript.code.type.Types;
import zzzank.probejs.lang.typescript.code.type.js.JSLambdaType;

import static zzzank.probejs.docs.recipes.BuiltinRecipeDocs.*;

/**
 * @author ZZZank
 */
class Thermal extends RecipeDocProvider {

    public static final BaseType MIXED_IN = Types.or(INGR, FLUID);
    public static final BaseType MIXED_OUT = Types.or(STACK, FLUID);

    public static JSLambdaType catalystStyleRecipe() {
        return recipeFn()
            .input(INGR)
            .returnType(classType("dev.latvian.kubejs.thermal.CatalystRecipeJS"))
            .build();
    }

    public static JSLambdaType fuelStyleRecipe() {
        return recipeFn()
            .input(INGR)
            .returnType(classType("dev.latvian.kubejs.thermal.FuelRecipeJS"))
            .build();
    }

    @Override
    public void addDocs(ScriptDump scriptDump) {
        //fuel
        add("compression_fuel", fuelStyleRecipe());
        add("lapidary_fuel", fuelStyleRecipe());
        add("magmatic_fuel", fuelStyleRecipe());
        add("numismatic_fuel", fuelStyleRecipe());
        add("stirling_fuel", fuelStyleRecipe());
        //catalyst
        add("insolator_catalyst", catalystStyleRecipe());
        add("pulverizer_catalyst", catalystStyleRecipe());
        add("smelter_catalyst", catalystStyleRecipe());
        //general
        val basicReturn = classType("dev.latvian.kubejs.thermal.BasicThermalRecipeJS");
        add("bottler",
            recipeFn().param("output", STACK)
                .param("input", selfOrArray(MIXED_IN))
                .returnType(basicReturn)
                .build()
        );
        add("brewer",
            recipeFn().param("output", FLUID)
                .param("input", selfOrArray(MIXED_IN))
                .returnType(basicReturn)
                .build()
        );
        add("centrifuge",
            recipeFn().param("output", selfOrArray(MIXED_OUT))
                .param("input", INGR)
                .returnType(basicReturn)
                .build()
        );
        add("crucible",
            recipeFn().param("output", FLUID).param("input", INGR).returnType(basicReturn).build()
        );
        add("furnance",
            recipeFn().param("output", STACK).param("input", INGR).returnType(basicReturn).build()
        );
        add("insolator",
            recipeFn().param("output", selfOrArray(STACK))
                .param("input", INGR)
                .returnType(basicReturn)
                .build()
        );
        add("press",
            recipeFn().param("outputs", selfOrArray(MIXED_OUT))
                .param("input", selfOrArray(INGR))
                .returnType(basicReturn)
                .build()
        );
        add("pulverizer",
            recipeFn().param("output", selfOrArray(STACK))
                .param("input", INGR)
                .returnType(basicReturn)
                .build()
        );
        add("pyrolyzer",
            recipeFn().param("outputs", selfOrArray(MIXED_OUT))
                .param("input", INGR)
                .returnType(basicReturn)
                .build()
        );
        add("refinery",
            recipeFn().param("outputs", selfOrArray(MIXED_OUT))
                .param("input", FLUID)
                .returnType(basicReturn)
                .build()
        );
        add("sawmill",
            recipeFn().param("outputs", selfOrArray(MIXED_OUT))
                .param("input", INGR)
                .returnType(basicReturn)
                .build()
        );
        add("smelter",
            recipeFn().param("outputs", selfOrArray(STACK))
                .param("inputs", selfOrArray(INGR))
                .returnType(basicReturn)
                .build()
        );
    }

    @Override
    public String namespace() {
        return "thermal";
    }

    @Override
    public boolean shouldEnable() {
        return super.shouldEnable() && Platform.isModLoaded("kubejs_thermal");
    }
}
