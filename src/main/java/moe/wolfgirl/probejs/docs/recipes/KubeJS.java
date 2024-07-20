package moe.wolfgirl.probejs.docs.recipes;

import dev.latvian.kubejs.fluid.FluidStackJS;
import dev.latvian.kubejs.item.ItemStackJS;
import moe.wolfgirl.probejs.lang.typescript.code.type.TSClassType;
import moe.wolfgirl.probejs.lang.typescript.code.type.Types;
import moe.wolfgirl.probejs.plugin.ProbeJSPlugin;
import net.minecraft.world.item.crafting.Ingredient;

/**
 * @author ZZZank
 */
public class KubeJS extends ProbeJSPlugin {

    public static final TSClassType FLUID = Types.type(FluidStackJS.class);
    public static final TSClassType STACK = Types.type(ItemStackJS.class);
    public static final TSClassType INGR = Types.type(Ingredient.class);
}
