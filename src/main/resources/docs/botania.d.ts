/**
 * @mod botania
 */
class RecipeHolder {
    botania: Document.BotaniaRecipes;
}

/**
 * @mod botania
 */
class BotaniaRecipes {
    runic_altar(output: Internal.ItemStackJS, inputs: Internal.IngredientJS[]): dev.latvian.kubejs.recipe.mod.BotaniaRunicAltarRecipeJS;
    runic_altar(output: Internal.ItemStackJS, inputs: Internal.IngredientJS[], mana: number): dev.latvian.kubejs.recipe.mod.BotaniaRunicAltarRecipeJS;
}