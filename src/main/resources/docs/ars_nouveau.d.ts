/**
 * @mod ars_nouveau
 */
class RecipeHolder {
    ars_nouveau: Document.ArsNouveauRecipes;
}

/**
 * @mod ars_nouveau
 */
class ArsNouveauRecipes {
    enchanting_apparatus(output: Internal.ItemStackJS, reagent: Internal.IngredientJS, inputs: Internal.IngredientJS[]): dev.latvian.kubejs.recipe.mod.ArsNouveauEnchantingApparatusRecipeJS;

    enchantment(enchantment: string, level: number, inputs: Internal.IngredientJS[]): dev.latvian.kubejs.recipe.mod.ArsNouveauEnchantmentRecipeJS;
    enchantment(enchantment: string, level: number, inputs: Internal.IngredientJS[], mana: number): dev.latvian.kubejs.recipe.mod.ArsNouveauEnchantmentRecipeJS;

    glyph_recipe(output: Internal.ItemStackJS, input: Internal.ItemStackJS, tier: string): dev.latvian.kubejs.recipe.mod.ArsNouveauEnchantmentRecipeJS;
}