
/**
 * @target dev.latvian.kubejs.recipe.RecipeEventJS
 */
class RecipeEventJS {
    /**
     * Holds all the recipes collected by probejs(not documents).
     */
    get recipes(): stub.probejs.recipeHolder;

    /**
     * @hidden
     */
    campfireCooking: dev.latvian.kubejs.recipe.RecipeFunction;
    /**
     * @hidden
     */
    smithing: dev.latvian.kubejs.recipe.RecipeFunction;
    /**
     * @hidden
     */
    stonecutting: dev.latvian.kubejs.recipe.RecipeFunction;
    /**
     * @hidden
     */
    shaped: dev.latvian.kubejs.recipe.RecipeFunction;
    /**
     * @hidden
     */
    smoking: dev.latvian.kubejs.recipe.RecipeFunction;
    /**
     * @hidden
     */
    shapeless: dev.latvian.kubejs.recipe.RecipeFunction;
    /**
     * @hidden
     */
    smelting: dev.latvian.kubejs.recipe.RecipeFunction;
    /**
     * @hidden
     */
    blasting: dev.latvian.kubejs.recipe.RecipeFunction;

    /**
     * Adds a smelting recipe to Minecraft.
     * Used by Furnaces.
     */
    smelting(output: Internal.ItemStackJS, input: Internal.IngredientJS): dev.latvian.kubejs.recipe.minecraft.CookingRecipeJS;
    /**
     * Adds a smelting recipe to Minecraft.
     * Used by Smokers.
     */
    smoking(output: Internal.ItemStackJS, input: Internal.IngredientJS): dev.latvian.kubejs.recipe.minecraft.CookingRecipeJS;
    /**
     * Adds a smelting recipe to Minecraft.
     * Used by Blast Furnaces.
     */
    blasting(output: Internal.ItemStackJS, input: Internal.IngredientJS): dev.latvian.kubejs.recipe.minecraft.CookingRecipeJS;

    /**
     * Adds a shaped crafting recipe.
     */
    shaped(output: Internal.ItemStackJS, pattern: string[], items: { [key: string]: Internal.IngredientJS_ }): dev.latvian.kubejs.recipe.minecraft.ShapedRecipeJS;
    /**
     * Adds a shapeless crafting recipe.
     */
    shapeless(output: Internal.ItemStackJS, inputs: Internal.IngredientJS[]): dev.latvian.kubejs.recipe.minecraft.ShapelessRecipeJS;
    /**
     * Adds a smelting recipe to Minecraft.
     * Used by Campfire.
     */
    campfireCooking(output: Internal.ItemStackJS, input: Internal.IngredientJS): dev.latvian.kubejs.recipe.minecraft.CookingRecipeJS;
    /**
     * Adds a stonecutting recipe.
     */
    stonecutting(output: Internal.ItemStackJS, inputs: Internal.IngredientJS): dev.latvian.kubejs.recipe.minecraft.StonecuttingRecipeJS;
    /**
     * Adds a smithing recipe.
     */
    smithing(output: Internal.ItemStackJS, base: Internal.IngredientJS, addition: Internal.IngredientJS): dev.latvian.kubejs.recipe.minecraft.SmithingRecipeJS;

}


class RecipeHolder {
    readonly minecraft: Document.MinecraftRecipes;
}


/**
 * All recipes from Minecraft.
 */
class MinecraftRecipes {
    /**
     * Adds a smelting recipe to Minecraft.
     * Used by Furnaces.
     */
    smelting(output: Internal.ItemStackJS, input: Internal.IngredientJS): dev.latvian.kubejs.recipe.minecraft.CookingRecipeJS;
    /**
     * Adds a smelting recipe to Minecraft.
     * Used by Smokers.
     */
    smoking(output: Internal.ItemStackJS, input: Internal.IngredientJS): dev.latvian.kubejs.recipe.minecraft.CookingRecipeJS;
    /**
     * Adds a smelting recipe to Minecraft.
     * Used by Blast Furnaces.
     */
    blasting(output: Internal.ItemStackJS, input: Internal.IngredientJS): dev.latvian.kubejs.recipe.minecraft.CookingRecipeJS;
    /**
     * Adds a shaped crafting recipe.
     */
    crafting_shaped(output: Internal.ItemStackJS, pattern: string[], items: { [key: string]: Internal.IngredientJS_ }): dev.latvian.kubejs.recipe.minecraft.ShapedRecipeJS;
    /**
     * Adds a shapeless crafting recipe.
     */
    crafting_shapeless(output: Internal.ItemStackJS, inputs: Internal.IngredientJS[]): dev.latvian.kubejs.recipe.minecraft.ShapelessRecipeJS;
    /**
     * Adds a smelting recipe to Minecraft.
     * Used by Campfire.
     */
    camefire_cooking(output: Internal.ItemStackJS, input: Internal.IngredientJS): dev.latvian.kubejs.recipe.minecraft.CookingRecipeJS;
    /**
     * Adds a stonecutting recipe.
     */
    stonecutting(output: Internal.ItemStackJS, inputs: Internal.IngredientJS): dev.latvian.kubejs.recipe.minecraft.StonecuttingRecipeJS;
    /**
     * Adds a smithing recipe.
     */
    smithing(output: Internal.ItemStackJS, base: Internal.IngredientJS, addition: Internal.IngredientJS): dev.latvian.kubejs.recipe.minecraft.SmithingRecipeJS;
}
