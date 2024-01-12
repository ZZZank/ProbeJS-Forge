
/**
 * Holds recipes documented for Extended Crafting
 * @mod extendedcrafting
 */
class extendedcraftingRecipes {
    shaped_table(output: dev.latvian.kubejs.item.ItemStackJS, pattern: string[], items: { [key: string]: Internal.IngredientJS_ }): dev.latvian.kubejs.recipe.minecraft.ShapedRecipeJS;
    shapeless_table(output: dev.latvian.kubejs.item.ItemStackJS, inputs: dev.latvian.kubejs.item.ingredient.IngredientJS[]): dev.latvian.kubejs.recipe.minecraft.ShapelessRecipeJS;
}