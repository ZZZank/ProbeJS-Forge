/**
 * @mod botanypots
 */
class RecipeHolder {
    botanypots: Document.BotanyPotsRecipes;
}

/**
 * @mod botanypots
 */
class BotanyPotsRecipes {
    /**
     * @param outputs any of the `ItemStackJS`, or `{item: ItemStackJS, minRolls: number, maxRolls: number}`
     */
    crop(outputs: object | Internal.ItemStackJS, input: Internal.IngredientJS): dev.latvian.kubejs.recipe.mod.BotanyPotsCropRecipeJS;
}