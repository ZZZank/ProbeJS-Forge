
/**
* @target dev.latvian.kubejs.item.ItemStackJS
* @assign string
* @assign object
*/
class ItemStackJS {
}

/**
 * @target dev.latvian.kubejs.item.ingredient.IngredientJS
 * @assign string
 * @assign object
 * @assign dev.latvian.kubejs.item.ItemStackJS
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
 * Represents the base Java Class
 * 
 * In JavaScript, `Class<T>`can be considered as `typeof T`
 * @target java.lang.Class
 */
class Class {
}

/**
 * Class `Object` is the root of the class hierarchy.
 * Every class has `Object` as a superclass. All objects,
 * including arrays, implement the methods of this class.
 * 
 * Any JavaScript object can be considered as `Object`
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
 * @target net.minecraft.world.item.enchantment.Enchantment
 * @assign string
 */
class Enchantment {
}

/**
 * @target dev.latvian.kubejs.block.MaterialJS
 * @assign string
 */
class MaterialJS {
}

/**
 * @target net.minecraft.nbt.CompoundNBT
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
 * @assign string
 */
class FluidStackJS {
}

/**
 * @target dev.latvian.mods.rhino.mod.util.color.Color
 * @assign string
 */
class Color {
}