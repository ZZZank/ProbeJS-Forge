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
    upgrade(output: Internal.ItemStackJS, pattern: string[], items: { [key: string]: Internal.IngredientJS_ }): dev.latvian.kubejs.recipe.minecraft.ShapedRecipeJS;
}