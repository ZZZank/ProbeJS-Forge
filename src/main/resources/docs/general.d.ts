
/**
 * @target dev.latvian.kubejs.recipe.ingredientaction.IngredientActionFilter
 * @assign number
 * @assign dev.latvian.kubejs.item.ingredient.IngredientJS
 * @assign {index: number, item: Internal.IngredientJS_}
 */
class IngredientActionFilter {
}

/**
 * @target net.minecraft.core.Vec3i
 * @assign [number, number, number]
 */
class Vec3i {
}

/**
 * @target net.minecraft.item.ItemStack
 * @assign Internal.ItemStackJS_
 */
class ItemStack {
}

/**
 * @target com.google.gson.JsonObject
 * @assign {}
 */
class JsonObject {
}

/**
* @target dev.latvian.kubejs.item.ItemStackJS
* @assign net.minecraft.item.Item
* @assign `${number}x ${string}`
* @assign object
*/
class ItemStackJS {
}

/**
 * @target dev.latvian.kubejs.item.ingredient.IngredientJS
 * 
 * @assign dev.latvian.kubejs.item.ItemStackJS
 * @assign dev.latvian.kubejs.fluid.FluidStackJS
 * @assign "*"
 * @assign "" | "-" | "air" | "minecraft:air"
 * @assign `%${string}`
 * @assign `@${platform.modids}`
 * @assign `#${Tag.item}`
 * @assign RegExp
 * @assign Internal.Ingredient
 * @assign dev.latvian.kubejs.item.ingredient.IngredientJS[]
 * @assign {type: string}
 * @assign {item: Internal.ItemStackJS_, count?: number}
 * @assign {fluid: Internal.FluidStackJS_}
 * @assign {value: object}
 * @assign {ingredient: object}
 * Represents an Ingredient, which can match one or multiple ItemStacks.
 * 
 * Can be casted from several object, which has different usages.
 * 
 * If you want to specify nbt to check in ItemStack, use either Item.of() or {type: "forge:nbt"}.
 * 
 * Using {item: ItemStackJS} will NOT preserve NBT in any form.
 */
class IngredientJS {
}

/**
 * @target net.minecraft.util.text.ITextComponent
 * @assign string
 * @assign object
 * @assign dev.latvian.kubejs.text.Text
 */
class ITextComponent {
}

/**
 * @target dev.latvian.kubejs.entity.EntityJS
 */
class EntityJS {
    /**
     * @hidden
     */
    getServer(): net.minecraft.server.MinecraftServer
}

/**
 * @target net.minecraft.util.ResourceLocation
 * @assign string
 */
class ResourceLocation {
}

/**
 * @target net.minecraft.nbt.CompoundTag
 * @assign string
 * @assign {[x in string]: (string | number | boolean | Internal.CompoundTag_)}
 */
class CompoundTag {
}

/**
 * @target java.util.Map
 * @assign {[key in K]: V}
 */
class Map {
}

/**
 * @target dev.latvian.kubejs.text.Text
 * @assign string
 */
class Text {
}

/**
 * @target java.lang.Class
 * Represents the base Java Class
 * 
 * In JavaScript, `Class<T>`can be considered as `typeof T`
 */
class Class {
}

/**
 * Class `Object` is the root of the class hierarchy.
 * Every class has `Object` as a superclass. All objects,
 * including arrays, implement the methods of this class.
 * 
 * Any Java object can be considered as `Object`
 */
class Object {
    getClass(): Internal.Class<Document.Object>;
    wait(arg0: number): void;
    wait(arg0: number, arg1: number): void;
    wait(): void;
    hashCode(): number;
    notifyAll(): void;
    equals(arg0: any): boolean;
    toString(): string;
    notify(): void;
    get class(): Internal.Class<Document.Object>;
}

/**
 * @target net.minecraft.nbt.CompoundNBT
 * @assign {[x in string]: (string | number | boolean | Internal.CompoundNBT_)}
 * @assign string
 * @assign object
 */
class CompoundNBT {
    [x: string]: any;
}

/**
 * @target dev.latvian.kubejs.util.AttachedData
 */
class AttachedData {
    [x: string]: any;
}

/**
 * @target dev.latvian.kubejs.fluid.FluidStackJS
 * @assign net.minecraft.fluid.Fluid
 */
class FluidStackJS {
}

/**
 * @target dev.latvian.mods.rhino.mod.util.color.Color
 * @assign string
 */
class Color {
}

/**
 * @target dev.latvian.kubejs.recipe.filter.RecipeFilter
 * @assign dev.latvian.kubejs.recipe.filter.RecipeFilter[]
 * @assign {exact?: boolean, not?: Internal.RecipeFilter_, or?: Internal.RecipeFilter_[], id?: string | RegExp, type?: string, group?: string, mod?: string, input?: Internal.IngredientJS_, output?: Internal.IngredientJS_}
 */
class RecipeFilter {
}

/**
 * @target dev.latvian.kubejs.block.BlockStatePredicate
 * @assign dev.latvian.kubejs.block.BlockStatePredicate[]
 * @assign {or?: Internal.BlockStatePredicate_, not?: Internal.BlockStatePredicate_}
 * @assign net.minecraft.block.Block
 * @assign net.minecraft.block.BlockState
 * @assign `#${Tag.block}`
 * @assign RegExp
 */
class BlockStatePredicate {
}
