/**
 * @mod cucumber
 */
class RecipeHolder extends stub.probejs.RecipeHolder {
    readonly cucumber: Document.CucumberRecipes;
}

/**
 * @mod cucumber
 */
class CucumberRecipes extends stub.probejs.cucumber {
    shaped_no_mirror(output: dev.latvian.kubejs.item.ItemStackJS, pattern: string[], items: { [x in string]: dev.latvian.kubejs.item.ingredient.IngredientJS }): dev.latvian.kubejs.recipe.minecraft.ShapedRecipeJS;
}