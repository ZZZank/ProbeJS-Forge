/**
 * @mod ars_nouveau
 */
class RecipeHolder extends stub.probejs.RecipeHolder {
    readonly ars_nouveau: Document.ArsNouveauRecipes;
}

/**
 * @mod ars_nouveau
 */
class ArsNouveauRecipes extends stub.probejs.ars_nouveau {
    enchanting_apparatus(output: dev.latvian.kubejs.item.ItemStackJS, reagent: dev.latvian.kubejs.item.ingredient.IngredientJS, inputs: dev.latvian.kubejs.item.ingredient.IngredientJS[]): dev.latvian.kubejs.recipe.mod.ArsNouveauEnchantingApparatusRecipeJS;

    enchantment(enchantment: string, level: number, inputs: dev.latvian.kubejs.item.ingredient.IngredientJS[]): dev.latvian.kubejs.recipe.mod.ArsNouveauEnchantmentRecipeJS;
    enchantment(enchantment: string, level: number, inputs: dev.latvian.kubejs.item.ingredient.IngredientJS[], mana: number): dev.latvian.kubejs.recipe.mod.ArsNouveauEnchantmentRecipeJS;

    glyph_recipe(output: dev.latvian.kubejs.item.ItemStackJS, input: dev.latvian.kubejs.item.ItemStackJS, tier: string): dev.latvian.kubejs.recipe.mod.ArsNouveauEnchantmentRecipeJS;
}