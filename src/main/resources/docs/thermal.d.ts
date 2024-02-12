/**
 * @mod thermal
 * @mod kubejs_thermal
 */
class RecipeHolder extends stub.probejs.RecipeHolder {
    /**
     * Recipes from Thermal Series
     */
    readonly thermal: Document.ThermalRecipes;
}

/**
 * @mod thermal
 * @mod kubejs_thermal
 */
type TEMixedOutput = dev.latvian.kubejs.item.ItemStackJS | dev.latvian.kubejs.fluid.FluidStackJS

/**
 * @mod thermal
 * @mod kubejs_thermal
 */
type TEMixedInput = dev.latvian.kubejs.item.ingredient.IngredientJS | dev.latvian.kubejs.fluid.FluidStackJS

/**
 * @mod thermal
 * @mod kubejs_thermal
 */
class ThermalRecipes extends stub.probejs.thermal {
    bottler(output: dev.latvian.kubejs.item.ItemStackJS, input: Type.SelfOrArray<Type.TEMixedInput>): dev.latvian.kubejs.thermal.BasicThermalRecipeJS;
    brewer(output: dev.latvian.kubejs.fluid.FluidStackJS, input: Type.SelfOrArray<Type.TEMixedInput>): dev.latvian.kubejs.thermal.BasicThermalRecipeJS;
    centrifuge(outputs: Type.SelfOrArray<Type.TEMixedOutput>, input: dev.latvian.kubejs.item.ingredient.IngredientJS): dev.latvian.kubejs.thermal.BasicThermalRecipeJS;
    compression_fuel(input: dev.latvian.kubejs.item.ingredient.IngredientJS): dev.latvian.kubejs.thermal.FuelRecipeJS;
    crucible(output: dev.latvian.kubejs.fluid.FluidStackJS, input: dev.latvian.kubejs.item.ingredient.IngredientJS): dev.latvian.kubejs.thermal.BasicThermalRecipeJS;
    furnace(output: dev.latvian.kubejs.item.ItemStackJS, input: dev.latvian.kubejs.item.ingredient.IngredientJS): dev.latvian.kubejs.thermal.BasicThermalRecipeJS;
    insolator(output: Type.SelfOrArray<dev.latvian.kubejs.item.ItemStackJS>, input: dev.latvian.kubejs.item.ingredient.IngredientJS): dev.latvian.kubejs.thermal.InsolatorRecipeJS;
    insolator_catalyst(input: dev.latvian.kubejs.item.ingredient.IngredientJS): dev.latvian.kubejs.thermal.CatalystRecipeJS;
    lapidary_fuel(input: dev.latvian.kubejs.item.ingredient.IngredientJS): dev.latvian.kubejs.thermal.FuelRecipeJS;
    magmatic_fuel(input: dev.latvian.kubejs.item.ingredient.IngredientJS): dev.latvian.kubejs.thermal.FuelRecipeJS;
    numismatic_fuel(input: dev.latvian.kubejs.item.ingredient.IngredientJS): dev.latvian.kubejs.thermal.FuelRecipeJS;
    press(outputs: Type.SelfOrArray<Type.TEMixedOutput>, input: Type.SelfOrArray<dev.latvian.kubejs.item.ingredient.IngredientJS>): dev.latvian.kubejs.thermal.BasicThermalRecipeJS;
    pulverizer(outputs: Type.SelfOrArray<dev.latvian.kubejs.item.ItemStackJS>, input: dev.latvian.kubejs.item.ingredient.IngredientJS): dev.latvian.kubejs.thermal.BasicThermalRecipeJS;
    pulverizer_catalyst(input: dev.latvian.kubejs.item.ingredient.IngredientJS): dev.latvian.kubejs.thermal.CatalystRecipeJS;
    pyrolyzer(outputs: Type.SelfOrArray<Type.TEMixedOutput>, input: dev.latvian.kubejs.item.ingredient.IngredientJS): dev.latvian.kubejs.thermal.BasicThermalRecipeJS;
    refinery(outputs: Type.SelfOrArray<Type.TEMixedOutput>, input: dev.latvian.kubejs.fluid.FluidStackJS): dev.latvian.kubejs.thermal.BasicThermalRecipeJS;
    sawmill(outputs: Type.SelfOrArray<dev.latvian.kubejs.item.ItemStackJS>, input: dev.latvian.kubejs.item.ingredient.IngredientJS): dev.latvian.kubejs.thermal.BasicThermalRecipeJS;
    smelter(outputs: Type.SelfOrArray<dev.latvian.kubejs.item.ItemStackJS>, input: Type.SelfOrArray<dev.latvian.kubejs.item.ingredient.IngredientJS>): dev.latvian.kubejs.thermal.BasicThermalRecipeJS;
    smelter_catalyst(input: dev.latvian.kubejs.item.ingredient.IngredientJS): dev.latvian.kubejs.thermal.CatalystRecipeJS;
    stirling_fuel(input: dev.latvian.kubejs.item.ingredient.IngredientJS): dev.latvian.kubejs.thermal.FuelRecipeJS;
}
