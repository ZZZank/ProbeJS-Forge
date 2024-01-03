/**
* @target Internal.ItemStackJS
* @assign string
* @assign object
*/
class ItemStackJS {
}

/**
 * @target Internal.IngredientJS
 * @assign string
 * @assign object
 * @assign Internal.ItemStackJS
 */
class IngredientJS {

}

/**
* @target dev.latvian.kubejs.recipe.RecipeEventJS
*/
class RecipeEventJS {
    /**
     * Holds all the recipes collected from documents.
     * @returns Document.RecipeHolder
     */
    getRecipes(): java.util.Map<java.lang.String, java.lang.Object>;


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
     * 
     * This is used by Furnaces.
     */
    smelting(output: Internal.ItemStackJS, input: Internal.IngredientJS): dev.latvian.kubejs.recipe.minecraft.CookingRecipeJS;
    /**
     * Adds a smelting recipe to Minecraft.
     * 
     * This is used by Smokers.
     */
    smoking(output: Internal.ItemStackJS, input: Internal.IngredientJS): dev.latvian.kubejs.recipe.minecraft.CookingRecipeJS;
    /**
     * Adds a smelting recipe to Minecraft.
     * 
     * This is used by Blast Furnaces.
     */
    blasting(output: Internal.ItemStackJS, input: Internal.IngredientJS): dev.latvian.kubejs.recipe.minecraft.CookingRecipeJS;

    /**
     * Adds a shaped crafting recipe.
     */
    shaped(output: Internal.ItemStackJS, pattern: string[], items: java.util.Map<string, Internal.IngredientJS>): dev.latvian.kubejs.recipe.minecraft.ShapedRecipeJS;
    /**
     * Adds a shapeless crafting recipe.
     */
    shapeless(output: Internal.ItemStackJS, inputs: Internal.IngredientJS[]): dev.latvian.kubejs.recipe.minecraft.ShapelessRecipeJS;
    /**
     * Adds a smelting recipe to Minecraft.
     * 
     * This is used by Camefire.
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
    /**
     * All recipes from Minecraft.
     */
    readonly minecraft: Document.MinecraftRecipes;
}


class MinecraftRecipes {
    /**
     * Adds a smelting recipe to Minecraft.
     * 
     * This is used by Furnaces.
     */
    smelting(output: Internal.ItemStackJS, input: Internal.IngredientJS): dev.latvian.kubejs.recipe.minecraft.CookingRecipeJS;
    /**
     * Adds a smelting recipe to Minecraft.
     * 
     * This is used by Smokers.
     */
    smoking(output: Internal.ItemStackJS, input: Internal.IngredientJS): dev.latvian.kubejs.recipe.minecraft.CookingRecipeJS;
    /**
     * Adds a smelting recipe to Minecraft.
     * 
     * This is used by Blast Furnaces.
     */
    blasting(output: Internal.ItemStackJS, input: Internal.IngredientJS): dev.latvian.kubejs.recipe.minecraft.CookingRecipeJS;

    /**
     * Adds a shaped crafting recipe.
     */
    crafting_shaped(output: Internal.ItemStackJS, pattern: string[], items: java.util.Map<string, Internal.IngredientJS>): dev.latvian.kubejs.recipe.minecraft.ShapedRecipeJS;
    /**
     * Adds a shapeless crafting recipe.
     */
    crafting_shapeless(output: Internal.ItemStackJS, inputs: Internal.IngredientJS[]): dev.latvian.kubejs.recipe.minecraft.ShapelessRecipeJS;
    /**
     * Adds a smelting recipe to Minecraft.
     * 
     * This is used by Camefire.
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
