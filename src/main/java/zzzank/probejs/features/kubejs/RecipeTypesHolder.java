package zzzank.probejs.features.kubejs;

import dev.latvian.kubejs.recipe.RecipeTypeJS;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

/**
 * @author ZZZank
 */
public interface RecipeTypesHolder {

    Map<ResourceLocation, RecipeTypeJS> pjs$recipeTypes();
}
