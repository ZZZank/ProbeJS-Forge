/**
 * @mod cucumber
 */
class RecipeHolder {
    cucumber: Document.CucumberRecipes;
}

/**
 * @mod cucumber
 */
class CucumberRecipes {
    shaped_no_mirror(output: dev.latvian.kubejs.item.ItemStackJS, pattern: string[], items: { [key in string]: dev.latvian.kubejs.item.ingredient.IngredientJS }): dev.latvian.kubejs.recipe.minecraft.ShapedRecipeJS;
}