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
    shaped_table(output: Internal.ItemStackJS, pattern: string[], items: { [key: string]: Internal.IngredientJS_ }): dev.latvian.kubejs.recipe.minecraft.ShapedRecipeJS;
    shapeless_table(output: Internal.ItemStackJS, inputs: Internal.IngredientJS[]): dev.latvian.kubejs.recipe.minecraft.ShapelessRecipeJS;
}