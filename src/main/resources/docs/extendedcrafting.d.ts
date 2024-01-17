/**
 * @mod extendedcrafting
 */
class RecipeHolder {
    extendedcrafting: Document.ExtendedCraftingRecipes;
}

/**
 * @mod extendedcrafting
 */
class ExtendedCraftingRecipes {
    shaped_table(output: dev.latvian.kubejs.item.ItemStackJS, pattern: string[], items: { [key in string]: dev.latvian.kubejs.item.ingredient.IngredientJS }): dev.latvian.kubejs.recipe.minecraft.ShapedRecipeJS;
    shapeless_table(output: dev.latvian.kubejs.item.ItemStackJS, inputs: dev.latvian.kubejs.item.ingredient.IngredientJS[]): dev.latvian.kubejs.recipe.minecraft.ShapelessRecipeJS;
}