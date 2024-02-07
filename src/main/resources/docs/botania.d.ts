/**
 * @mod botania
 */
class RecipeHolder extends stub.probejs.RecipeHolder {
    readonly botania: Document.BotaniaRecipes;
}

/**
 * @mod botania
 */
class BotaniaRecipes extends stub.probejs.botania {
    runic_altar(output: dev.latvian.kubejs.item.ItemStackJS, inputs: dev.latvian.kubejs.item.ingredient.IngredientJS[]): dev.latvian.kubejs.recipe.mod.BotaniaRunicAltarRecipeJS;
    runic_altar(output: dev.latvian.kubejs.item.ItemStackJS, inputs: dev.latvian.kubejs.item.ingredient.IngredientJS[], mana: number): dev.latvian.kubejs.recipe.mod.BotaniaRunicAltarRecipeJS;
}