package zzzank.probejs.docs.recipes;

import lombok.val;
import me.shedaniel.architectury.platform.Platform;
import zzzank.probejs.lang.typescript.ScriptDump;
import zzzank.probejs.lang.typescript.code.type.TSArrayType;
import zzzank.probejs.lang.typescript.code.type.Types;
import zzzank.probejs.lang.typescript.code.type.js.JSJoinedType;

import static zzzank.probejs.docs.recipes.RecipeDocUtil.*;

/**
 * @author ZZZank
 */
class Create extends RecipeDocProvider {

    private static final JSJoinedType.Union INGR_FLUID = INGR.or(FLUID);
    private static final JSJoinedType.Union STACK_FLUID = STACK.or(FLUID);
    private static final TSArrayType INGR_FLUID_N = INGR_FLUID.asArray();

    @Override
    public void addDocs(ScriptDump scriptDump) {
        val procReturn = classType("dev.latvian.kubejs.create.ProcessingRecipeJS");
        add("crushing", recipeFn().outputs(STACK_N).input(INGR).returnType(procReturn));
        add("milling", recipeFn().outputs(STACK_N).input(INGR).returnType(procReturn));
        add(
            "compacting",
            recipeFn()
                .output(STACK_FLUID)
                .inputs(INGR_FLUID_N)
                .returnType(procReturn)
        );
        add(
            "mixing",
            recipeFn()
                .output(STACK_FLUID)
                .inputs(INGR_FLUID_N)
                .returnType(procReturn)
        );
        add("pressing",
            recipeFn()
                .output(STACK)
                .input(INGR)
                .returnType(procReturn)
        );
        add("deploying",
            recipeFn()
                .output(STACK)
                .input(INGR)
                .returnType(procReturn)
        );
        add(
            "cutting",
            recipeFn().output(STACK).input(INGR).returnType(procReturn)
        );
        add(
            "filling",
            recipeFn()
                .output(STACK)
                .param("input", Types.tuple().member("fluid", FLUID).member("base", INGR).build())
                .returnType(procReturn)
        );
        add(
            "sequenced_assembly",
            recipeFn()
                .output(STACK_N)
                .input(INGR)
                .param("sequence", procReturn.asArray())
                .returnType(classType("dev.latvian.kubejs.create.SequencedAssemblyRecipeJS"))
        );
        add("splashing", recipeFn()
            .output(STACK_N)
            .input(INGR)
            .returnType(procReturn)
        );
        add(
            "sandpaper_polishing",
            recipeFn()
                .output(STACK)
                .input(INGR)
                .returnType(procReturn)
        );
        add("mechanical_crafting", basicShapedRecipe(procReturn));
        add(
            "emptying",
            recipeFn()
                .param("outputs", Types.tuple().member("item", STACK).member("fluid", FLUID).build())
                .input(INGR)
                .returnType(procReturn)
        );
    }

    @Override
    public String namespace() {
        return "create";
    }

    @Override
    public boolean shouldEnable() {
        return Platform.isModLoaded("kubejs_create") && super.shouldEnable();
    }
}
