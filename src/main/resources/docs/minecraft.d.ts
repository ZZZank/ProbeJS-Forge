
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
    smelting(output: dev.latvian.kubejs.item.ItemStackJS, input: dev.latvian.kubejs.item.ingredient.IngredientJS): dev.latvian.kubejs.recipe.minecraft.CookingRecipeJS;
    /**
     * Adds a smelting recipe to Minecraft.
     * Used by Smokers.
     */
    smoking(output: dev.latvian.kubejs.item.ItemStackJS, input: dev.latvian.kubejs.item.ingredient.IngredientJS): dev.latvian.kubejs.recipe.minecraft.CookingRecipeJS;
    /**
     * Adds a smelting recipe to Minecraft.
     * Used by Blast Furnaces.
     */
    blasting(output: dev.latvian.kubejs.item.ItemStackJS, input: dev.latvian.kubejs.item.ingredient.IngredientJS): dev.latvian.kubejs.recipe.minecraft.CookingRecipeJS;

    /**
     * Adds a shaped crafting recipe.
     */
    shaped(output: dev.latvian.kubejs.item.ItemStackJS, pattern: string[], items: { [x in string]: Internal.IngredientJS_ }): dev.latvian.kubejs.recipe.minecraft.ShapedRecipeJS;
    /**
     * Adds a shapeless crafting recipe.
     */
    shapeless(output: dev.latvian.kubejs.item.ItemStackJS, inputs: dev.latvian.kubejs.item.ingredient.IngredientJS[]): dev.latvian.kubejs.recipe.minecraft.ShapelessRecipeJS;
    /**
     * Adds a smelting recipe to Minecraft.
     * Used by Campfire.
     */
    campfireCooking(output: dev.latvian.kubejs.item.ItemStackJS, input: dev.latvian.kubejs.item.ingredient.IngredientJS): dev.latvian.kubejs.recipe.minecraft.CookingRecipeJS;
    /**
     * Adds a stonecutting recipe.
     */
    stonecutting(output: dev.latvian.kubejs.item.ItemStackJS, inputs: dev.latvian.kubejs.item.ingredient.IngredientJS): dev.latvian.kubejs.recipe.minecraft.StonecuttingRecipeJS;
    /**
     * Adds a smithing recipe.
     */
    smithing(output: dev.latvian.kubejs.item.ItemStackJS, base: dev.latvian.kubejs.item.ingredient.IngredientJS, addition: dev.latvian.kubejs.item.ingredient.IngredientJS): dev.latvian.kubejs.recipe.minecraft.SmithingRecipeJS;

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
    smelting(output: dev.latvian.kubejs.item.ItemStackJS, input: dev.latvian.kubejs.item.ingredient.IngredientJS): dev.latvian.kubejs.recipe.minecraft.CookingRecipeJS;
    /**
     * Adds a smelting recipe to Minecraft.
     * Used by Smokers.
     */
    smoking(output: dev.latvian.kubejs.item.ItemStackJS, input: dev.latvian.kubejs.item.ingredient.IngredientJS): dev.latvian.kubejs.recipe.minecraft.CookingRecipeJS;
    /**
     * Adds a smelting recipe to Minecraft.
     * Used by Blast Furnaces.
     */
    blasting(output: dev.latvian.kubejs.item.ItemStackJS, input: dev.latvian.kubejs.item.ingredient.IngredientJS): dev.latvian.kubejs.recipe.minecraft.CookingRecipeJS;
    /**
     * Adds a shaped crafting recipe.
     */
    crafting_shaped(output: dev.latvian.kubejs.item.ItemStackJS, pattern: string[], items: { [x in string]: Internal.IngredientJS_ }): dev.latvian.kubejs.recipe.minecraft.ShapedRecipeJS;
    /**
     * Adds a shapeless crafting recipe.
     */
    crafting_shapeless(output: dev.latvian.kubejs.item.ItemStackJS, inputs: dev.latvian.kubejs.item.ingredient.IngredientJS[]): dev.latvian.kubejs.recipe.minecraft.ShapelessRecipeJS;
    /**
     * Adds a smelting recipe to Minecraft.
     * Used by Campfire.
     */
    camefire_cooking(output: dev.latvian.kubejs.item.ItemStackJS, input: dev.latvian.kubejs.item.ingredient.IngredientJS): dev.latvian.kubejs.recipe.minecraft.CookingRecipeJS;
    /**
     * Adds a stonecutting recipe.
     */
    stonecutting(output: dev.latvian.kubejs.item.ItemStackJS, inputs: dev.latvian.kubejs.item.ingredient.IngredientJS): dev.latvian.kubejs.recipe.minecraft.StonecuttingRecipeJS;
    /**
     * Adds a smithing recipe.
     */
    smithing(output: dev.latvian.kubejs.item.ItemStackJS, base: dev.latvian.kubejs.item.ingredient.IngredientJS, addition: dev.latvian.kubejs.item.ingredient.IngredientJS): dev.latvian.kubejs.recipe.minecraft.SmithingRecipeJS;
}
