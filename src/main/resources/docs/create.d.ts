/**
* @mod create
* @mod kubejs_create
*/
class RecipeHolder {
    /**
     * All recipes from Create.
     */
    readonly create: Document.CreateRecipes;
}

/**
* @mod create
* @mod kubejs_create
*/
type ItemStackOrFluid = Internal.ItemStackJS | dev.latvian.kubejs.fluid.FluidStackJS;
/**
* @mod create
* @mod kubejs_create
*/
type IngredientOrFluid = Internal.IngredientJS | dev.latvian.kubejs.fluid.FluidStackJS;

/**
* @mod create
* @mod kubejs_create
*/
class CreateRecipes {
    /**
     * Creates a recipe for Crushing Wheels.
     * 
     * Specifying chances on outputs will make them output with chance.
     */
    crushing(outputs: Internal.ItemStackJS[], input: Internal.IngredientJS): dev.latvian.kubejs.create.ProcessingRecipeJS;
    /**
     * Creates a recipe for Millstone.
     * 
     * Specifying chances on outputs will make them output with chance.
     */
    milling(outputs: Internal.ItemStackJS[], input: Internal.IngredientJS): dev.latvian.kubejs.create.ProcessingRecipeJS;
    /**
     * Creates a recipe for Compacting.
     */
    compacting(output: Type.ItemStackOrFluid, inputs: Type.IngredientOrFluid[]): dev.latvian.kubejs.create.ProcessingRecipeJS;
    /**
     * Creates a recipe for Mixing.
     */
    mixing(output: Type.ItemStackOrFluid, inputs: Type.IngredientOrFluid[]): dev.latvian.kubejs.create.ProcessingRecipeJS;
    /**
     * Creates a recipe for Pressing.
     * 
     * Pressing uses Depot or Belt as container, and can only have 1 item slot as input.
     * 
     * Pressing is available as an Assembly step.
     */
    pressing(output: Internal.ItemStackJS, input: Internal.IngredientJS): dev.latvian.kubejs.create.ProcessingRecipeJS;
    /**
     * Creates a recipe for Deploying.
     * 
     * Deploying is available as an Assembly step.
     */
    deploying(output: Internal.ItemStackJS, input: Internal.IngredientJS): dev.latvian.kubejs.create.ProcessingRecipeJS;
    /**
     * Creates a recipe for Cutting.
     * 
     * Cutting is available as an Assembly step.
     */
    cutting(output: Internal.ItemStackJS, input: Internal.IngredientJS): dev.latvian.kubejs.create.ProcessingRecipeJS;
    /**
     * Creates a recipe for Filling.
     * 
     * Filling is available as an Assembly step.
     */
    filling(output: Internal.ItemStackJS, input: Type.IngredientOrFluid[]): dev.latvian.kubejs.create.ProcessingRecipeJS;
    /**
     * Creates a recipe for Sequenced Assembly.
     * 
     * The sequnce must use recipes which is available for Assembly.
     */
    sequenced_assembly(output: Internal.ItemStackJS[], input: Internal.IngredientJS, sequence: dev.latvian.kubejs.create.ProcessingRecipeJS[]): dev.latvian.kubejs.create.SequencedAssemblyRecipeJS;
    /**
     * Creates a recipe for Splashing.
     */
    splashing(output: Internal.ItemStackJS[], input: Internal.IngredientJS): dev.latvian.kubejs.create.ProcessingRecipeJS;
    /**
     * Creates a recipe for Haunting.
     */
    haunting(output: Internal.ItemStackJS[], input: Internal.IngredientJS): dev.latvian.kubejs.create.ProcessingRecipeJS;
    /**
     * Creates a recipe for Sandpaper Polishing.
     */
    sandpaper_polishing(output: Internal.ItemStackJS, input: Internal.IngredientJS): dev.latvian.kubejs.create.ProcessingRecipeJS;
    /**
     * Creates a recipe for Mechanical Crafting.
     */
    mechanical_crafting(output: Internal.ItemStackJS, pattern: string[], items: java.util.Map<string, Internal.IngredientJS>): dev.latvian.kubejs.create.ProcessingRecipeJS;

    /**
     * Creates a recipe for Emptying.
     */
    emptying(output: Type.ItemStackOrFluid[], input: Internal.IngredientJS): dev.latvian.kubejs.create.ProcessingRecipeJS;
}
