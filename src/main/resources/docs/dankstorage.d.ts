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
    upgrade(output: dev.latvian.kubejs.item.ItemStackJS, pattern: string[], items: { [key in string]: dev.latvian.kubejs.item.ingredient.IngredientJS }): dev.latvian.kubejs.recipe.minecraft.ShapedRecipeJS;
}