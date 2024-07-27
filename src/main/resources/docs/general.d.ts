

/**
 * @target dev.latvian.kubejs.item.ingredient.IngredientJS
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
 * @assign {type: string, [x in string]: any}
 * @assign {item: dev.latvian.kubejs.item.ItemStackJS, count?: number}
 * @assign {fluid: dev.latvian.kubejs.fluid.FluidStackJS}
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
 * @target net.minecraft.nbt.CompoundTag
 * @assign string
 * @assign {[x in string]: (string | number | boolean | net.minecraft.nbt.CompoundTag)}
 */
class CompoundTag {
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
 * @assign {[x in string]: (string | number | boolean | net.minecraft.nbt.CompoundNBT)}
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