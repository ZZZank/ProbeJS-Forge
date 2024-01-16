
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
 * @assign {[string]: string | number | boolean | Internal.CompoundTag_}
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
 * @assign (typeof T)
 */
class Class {
}

/**
 * @target java.lang.Object
 * @assign any
 */
class Object {
}