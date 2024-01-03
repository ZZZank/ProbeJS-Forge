/**
 * @mod dankstorage
 */
 class RecipeHolder {
    dankstorage: Document.DankStorageRecipes;
}

/**
 * @mod dankstorage
 */
class DankStorageRecipes {
    upgrade(output: Internal.ItemStackJS, pattern: string[], items: java.util.Map<string, Internal.IngredientJS>): dev.latvian.kubejs.recipe.minecraft.ShapedRecipeJS;
}