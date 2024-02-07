/**
* @mod integrateddynamics
*/
class RecipeHolder extends stub.probejs.RecipeHolder {
    /**
     * The builtin support for Integrated Dynamics is not complete in KubeJS.
     * 
     * If you need Basin or Mechanical Basin, please use event.custom() .
     */
    readonly integrateddynamics: Document.IntegratedDynamicsRecipes;
}

/**
* @mod integrateddynamics
*/
class IntegratedDynamicsRecipes extends stub.probejs.integrateddynamics {
    /**
     * Adds a recipe of Squeezer.
     * 
     * @param output The outputs, **NOTE:** The first item in output must not have a chance. 
     */
    squeezer(outputs: dev.latvian.kubejs.item.ItemStackJS[], input: dev.latvian.kubejs.item.ingredient.IngredientJS): dev.latvian.kubejs.recipe.mod.IDSqueezerRecipeJS;
    /**
     * Adds a recipe of Mechanical Squeezer.
     * 
     * @param output The outputs, **NOTE:** The first item in output must not have a chance. 
     */
    mechanical_squeezer(outputs: dev.latvian.kubejs.item.ItemStackJS[], input: dev.latvian.kubejs.item.ingredient.IngredientJS): dev.latvian.kubejs.recipe.mod.IDSqueezerRecipeJS;
}
