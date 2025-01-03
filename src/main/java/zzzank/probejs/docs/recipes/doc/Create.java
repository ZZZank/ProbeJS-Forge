package zzzank.probejs.docs.recipes.doc;

import dev.latvian.kubejs.recipe.RecipeEventJS;
import lombok.val;
import me.shedaniel.architectury.platform.Platform;
import zzzank.probejs.docs.recipes.RecipeDocProvider;
import zzzank.probejs.lang.java.clazz.ClassPath;
import zzzank.probejs.lang.typescript.ScriptDump;
import zzzank.probejs.lang.typescript.TypeScriptFile;
import zzzank.probejs.lang.typescript.code.member.TypeDecl;
import zzzank.probejs.lang.typescript.code.type.Types;
import zzzank.probejs.lang.typescript.code.type.js.JSJoinedType;
import zzzank.probejs.lang.typescript.code.type.js.JSPrimitiveType;
import zzzank.probejs.lang.typescript.code.type.ts.TSArrayType;
import zzzank.probejs.lang.typescript.code.type.ts.TSClassType;

import java.util.Map;

import static zzzank.probejs.docs.recipes.RecipeDocUtil.*;

/**
 * @author ZZZank
 */
class Create extends RecipeDocProvider {

    private static final JSJoinedType.Union INGR_FLUID = INGR.or(FLUID);
    private static final JSJoinedType.Union STACK_FLUID = STACK.or(FLUID);
    private static final TSArrayType INGR_FLUID_N = INGR_FLUID.asArray();

    private static final TSClassType PROCESSING = classType("dev.latvian.kubejs.create.ProcessingRecipeJS");
    private static final JSPrimitiveType ASSEMBLY_CAPABLE_RECIPE = Types.primitive("CreateAssemblyCapableRecipeJS");
    private static final TSClassType SEQUENCED_ASSEMBLY = classType("dev.latvian.kubejs.create.SequencedAssemblyRecipeJS");

    @Override
    public void addDocs(ScriptDump scriptDump) {
        add("crushing", recipeFn().outputs(STACK_N).input(INGR).returnType(PROCESSING));
        add("milling", recipeFn().outputs(STACK_N).input(INGR).returnType(PROCESSING));
        add(
            "compacting",
            recipeFn()
                .output(STACK_FLUID)
                .inputs(INGR_FLUID_N)
                .returnType(PROCESSING)
        );
        add(
            "mixing",
            recipeFn()
                .output(STACK_FLUID)
                .inputs(INGR_FLUID_N)
                .returnType(PROCESSING)
        );
        add("pressing",
            recipeFn()
                .output(STACK)
                .input(INGR)
                .returnType(ASSEMBLY_CAPABLE_RECIPE)
        );
        add("deploying",
            recipeFn()
                .output(STACK)
                .input(INGR)
                .returnType(ASSEMBLY_CAPABLE_RECIPE)
        );
        add(
            "cutting",
            recipeFn().output(STACK).input(INGR).returnType(ASSEMBLY_CAPABLE_RECIPE)
        );
        add(
            "filling",
            recipeFn()
                .output(STACK)
                .param("input", Types.tuple().member("fluid", FLUID).member("base", INGR).build())
                .returnType(ASSEMBLY_CAPABLE_RECIPE)
        );
        add(
            "sequenced_assembly",
            recipeFn()
                .output(STACK_N)
                .input(INGR)
                .param("sequence", ASSEMBLY_CAPABLE_RECIPE.asArray())
                .returnType(SEQUENCED_ASSEMBLY)
        );
        add("splashing", recipeFn()
            .output(STACK_N)
            .input(INGR)
            .returnType(PROCESSING)
        );
        add(
            "sandpaper_polishing",
            recipeFn()
                .output(STACK)
                .input(INGR)
                .returnType(PROCESSING)
        );
        add("mechanical_crafting", basicShapedRecipe(PROCESSING));
        add(
            "emptying",
            recipeFn()
                .param("outputs", Types.tuple().member("item", STACK).member("fluid", FLUID).build())
                .input(INGR)
                .returnType(PROCESSING)
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

    @Override
    public void modifyClasses(ScriptDump scriptDump, Map<ClassPath, TypeScriptFile> globalClasses) {
        if (!shouldEnable()) {
            return;
        }
        val file = globalClasses.get(ClassPath.fromJava(RecipeEventJS.class));
        if (file == null) {
            return;
        }
        file.addCode(
            new TypeDecl(ASSEMBLY_CAPABLE_RECIPE.content, PROCESSING)
        );
    }
}
