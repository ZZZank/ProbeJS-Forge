
/**
 * Holds recipes documented for Botany Pots
 * @mod botanypots
 */
class botanypotsRecipes {
    /**
     * @param outputs any of the `ItemStackJS`, or `{item: ItemStackJS, minRolls: number, maxRolls: number}`
     */
    crop(outputs: object | dev.latvian.kubejs.item.ItemStackJS, input: dev.latvian.kubejs.item.ingredient.IngredientJS): dev.latvian.kubejs.recipe.mod.BotanyPotsCropRecipeJS;
}