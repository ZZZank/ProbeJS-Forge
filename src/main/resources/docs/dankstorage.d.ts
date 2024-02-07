/**
 * @mod dankstorage
 */
class RecipeHolder extends stub.probejs.RecipeHolder {
    readonly dankstorage: Document.DankStorageRecipes;
}

/**
 * @mod dankstorage
 */
class DankStorageRecipes extends stub.probejs.dankstorage {
    upgrade(output: dev.latvian.kubejs.item.ItemStackJS, pattern: string[], items: { [key in string]: dev.latvian.kubejs.item.ingredient.IngredientJS }): dev.latvian.kubejs.recipe.minecraft.ShapedRecipeJS;
}