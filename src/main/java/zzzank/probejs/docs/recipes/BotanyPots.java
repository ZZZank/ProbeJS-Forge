package zzzank.probejs.docs.recipes;

import dev.latvian.kubejs.recipe.mod.BotanyPotsCropRecipeJS;
import me.shedaniel.architectury.platform.Platform;
import zzzank.probejs.docs.Primitives;
import zzzank.probejs.lang.typescript.ScriptDump;
import zzzank.probejs.lang.typescript.code.type.Types;

import static zzzank.probejs.docs.recipes.KubeJS.INGR;
import static zzzank.probejs.docs.recipes.KubeJS.STACK;

/**
 * @author ZZZank
 */
public class BotanyPots extends RecipeDocProvider {
    @Override
    public void addDocs(ScriptDump scriptDump) {
        add("crop", recipeFn()
            .param(
                "outputs",
                STACK.or(Types.object()
                    .member("item", STACK)
                    .member("minRolls", Primitives.INTEGER)
                    .member("maxRolls", Primitives.INTEGER)
                    .build())
            )
            .param("input", INGR)
            .returnType(Types.type(BotanyPotsCropRecipeJS.class))
            .build()
        );
    }

    @Override
    public String namespace() {
        return "botanypots";
    }
}
