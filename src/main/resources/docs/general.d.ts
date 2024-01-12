
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

