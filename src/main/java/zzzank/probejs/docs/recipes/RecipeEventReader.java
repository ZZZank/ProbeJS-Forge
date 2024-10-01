package zzzank.probejs.docs.recipes;

import dev.latvian.kubejs.KubeJSPlugin;
import dev.latvian.kubejs.recipe.RecipeEventJS;
import dev.latvian.kubejs.recipe.RecipeFunction;
import dev.latvian.kubejs.recipe.RecipeTypeJS;
import dev.latvian.kubejs.recipe.RegisterRecipeHandlersEvent;
import dev.latvian.kubejs.util.KubeJSPlugins;
import lombok.val;
import net.minecraft.resources.ResourceLocation;
import zzzank.probejs.lang.typescript.code.type.Types;
import zzzank.probejs.lang.typescript.code.type.js.JSObjectType;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ZZZank
 */
public class RecipeEventReader {
    public final JSObjectType.Builder builder = Types.object();

    public static Map<ResourceLocation, RecipeTypeJS> readFromPlugins() {
        val types = new HashMap<ResourceLocation, RecipeTypeJS>();
        val event = new RegisterRecipeHandlersEvent(types);
        KubeJSPlugins.forEachPlugin(kubeJSPlugin -> kubeJSPlugin.addRecipes(event));
        return types;
    }

    public void read(RecipeEventJS event) {
        val recipes = event.getRecipes();
        for (val entry : recipes.entrySet()) {
            val key = entry.getKey();
            val value = entry.getValue();
            if (value instanceof Map<?,?> nested) {
                scanNested(key, nested);
            } else if (value instanceof RecipeFunction recipeFn) {

            } else {
                //what?
            }
        }
    }

    private void scanNested(String parent, Map<?, ?> map) {

    }

    private void scanFn(String parent, RecipeFunction fn) {

    }
}
