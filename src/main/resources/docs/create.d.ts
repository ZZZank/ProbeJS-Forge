/**
* @mod create
* @mod kubejs_create
*/
class RecipeHolder extends stub.probejs.RecipeHolder {
    /**
     * Recipes from Create.
     */
    readonly create: Document.CreateRecipes;
}

///notes for those who're working on recipe doc:
///     `ItemStackOrFluid[]` or `IngredientOrFluid[]` should not be used anymore
///     because members of used array are fixed, not dynamic
///     use `Types.tuple()`

/**
* @mod create
* @mod kubejs_create
*/
type ItemStackOrFluid = dev.latvian.kubejs.item.ItemStackJS | dev.latvian.kubejs.fluid.FluidStackJS;
/**
* @mod create
* @mod kubejs_create
*/
type IngredientOrFluid = dev.latvian.kubejs.item.ingredient.IngredientJS | dev.latvian.kubejs.fluid.FluidStackJS;

/**
* @mod create
* @mod kubejs_create
*/
class CreateRecipes extends stub.probejs.create {
    /**
     * Pressing is available as an Assembly step.
     * Deploying is available as an Assembly step.
     * Cutting is available as an Assembly step.
     * Filling is available as an Assembly step.
     */
}
