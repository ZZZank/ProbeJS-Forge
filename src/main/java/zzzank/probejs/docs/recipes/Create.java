package zzzank.probejs.docs.recipes;

import lombok.val;
import me.shedaniel.architectury.platform.Platform;
import zzzank.probejs.lang.typescript.ScriptDump;
import zzzank.probejs.lang.typescript.code.type.TSArrayType;
import zzzank.probejs.lang.typescript.code.type.Types;
import zzzank.probejs.lang.typescript.code.type.js.JSJoinedType;

import static zzzank.probejs.docs.recipes.BuiltinRecipeDocs.*;

/**
 * @author ZZZank
 */
public class Create extends RecipeDocProvider {

    private static final JSJoinedType.Union INGR_FLUID = INGR.or(FLUID);
    private static final JSJoinedType.Union STACK_FLUID = STACK.or(FLUID);
    private static final TSArrayType INGR_FLUID_N = INGR_FLUID.asArray();

    @Override
    public void addDocs(ScriptDump scriptDump) {
        val procReturn = classType("dev.latvian.kubejs.create.ProcessingRecipeJS");
        add("crushing", recipeFn().param("outputs", STACK_N).param("input", INGR).returnType(procReturn));
        add("milling", recipeFn().param("outputs", STACK_N).param("input", INGR).returnType(procReturn));
        add(
            "compacting",
            recipeFn()
                .param("output", STACK_FLUID)
                .param("inputs", INGR_FLUID_N)
                .returnType(procReturn)
        );
        add(
            "mixing",
            recipeFn()
                .param("output", STACK_FLUID)
                .param("inputs", INGR_FLUID_N)
                .returnType(procReturn)
        );
        add("pressing",
            recipeFn()
                .param("output", STACK)
                .param("input", INGR)
                .returnType(procReturn)
        );
        add("deploying",
            recipeFn()
                .param("output", STACK)
                .param("input", INGR)
                .returnType(procReturn)
        );
        add(
            "cutting",
            recipeFn().param("output", STACK).param("input", INGR).returnType(procReturn)
        );
        add(
            "filling",
            recipeFn()
                .param("output", STACK)
                .param("input", Types.tuple().member("fluid", FLUID).member("base", INGR).build())
                .returnType(procReturn)
        );
        add(
            "sequenced_assembly",
            recipeFn()
                .param("output", STACK_N)
                .param("input", INGR)
                .param("sequence", procReturn.asArray())
                .returnType(classType("dev.latvian.kubejs.create.SequencedAssemblyRecipeJS"))
        );
        add("splashing", recipeFn()
            .param("output", STACK_N)
            .param("input", INGR)
            .returnType(procReturn)
        );
        add(
            "sandpaper_polishing",
            recipeFn()
                .param("output", STACK)
                .param("input", INGR)
                .returnType(procReturn)
        );
        add("mechanical_crafting", basicShapedRecipe(procReturn));
        add(
            "emptying",
            recipeFn()
                .param("outputs", Types.tuple().member("item", STACK).member("fluid", FLUID).build())
                .param("input", INGR)
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
