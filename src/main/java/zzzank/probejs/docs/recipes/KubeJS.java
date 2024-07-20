package zzzank.probejs.docs.recipes;

import dev.latvian.kubejs.fluid.FluidStackJS;
import dev.latvian.kubejs.item.ItemStackJS;
import net.minecraft.world.item.crafting.Ingredient;
import zzzank.probejs.lang.typescript.code.type.TSClassType;
import zzzank.probejs.lang.typescript.code.type.Types;
import zzzank.probejs.plugin.ProbeJSPlugin;

/**
 * @author ZZZank
 */
public class KubeJS extends ProbeJSPlugin {

    public static final TSClassType FLUID = Types.type(FluidStackJS.class);
    public static final TSClassType STACK = Types.type(ItemStackJS.class);
    public static final TSClassType INGR = Types.type(Ingredient.class);
}
