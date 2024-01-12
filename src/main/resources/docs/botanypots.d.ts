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
    crop(outputs: object | dev.latvian.kubejs.item.ItemStackJS, input: dev.latvian.kubejs.item.ingredient.IngredientJS): dev.latvian.kubejs.recipe.mod.BotanyPotsCropRecipeJS;
}