package moe.wolfgirl.probejs.docs.assignments;

import dev.latvian.kubejs.fluid.FluidStackJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.text.Text;
import moe.wolfgirl.probejs.docs.Primitives;
import moe.wolfgirl.probejs.lang.typescript.ScriptDump;
import moe.wolfgirl.probejs.lang.typescript.code.type.Types;
import moe.wolfgirl.probejs.plugin.ProbeJSPlugin;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluid;

/**
 * @author ZZZank
 */
public class KubeWrappers extends ProbeJSPlugin {

    @Override
    public void assignType(ScriptDump scriptDump) {

        scriptDump.assignType(Text.class, Types.ANY);

        scriptDump.assignType(ItemStackJS.class, Types.type(Item.class));
        scriptDump.assignType(ItemStackJS.class, "ItemWithCount", Types.object()
            .member("item", Types.primitive("Special.Item"))
            .member("count", true, Primitives.INTEGER)
            .build());

        scriptDump.assignType(IngredientJS.class, Types.type(ItemStack.class));
        scriptDump.assignType(IngredientJS.class, Types.type(Ingredient.class).asArray());

        scriptDump.assignType(IngredientJS.class, Types.primitive("RegExp"));
        scriptDump.assignType(IngredientJS.class, Types.literal("*"));
        scriptDump.assignType(IngredientJS.class, Types.literal("-"));
        scriptDump.assignType(IngredientJS.class, Types.primitive("`#${Special.ItemTag}`"));
        scriptDump.assignType(IngredientJS.class, Types.primitive("`@${Special.Mod}`"));
        scriptDump.assignType(IngredientJS.class, Types.primitive("`%${Special.CreativeModeTab}`"));

        scriptDump.assignType(FluidStackJS.class, Types.type(Fluid.class));
        scriptDump.assignType(FluidStackJS.class, Types.literal("-"));
        scriptDump.assignType(FluidStackJS.class, "FluidWithAmount", Types.object()
            .member("fluid", Types.primitive("Special.Fluid"))
            .member("amount", true, Primitives.INTEGER)
            .member("nbt", true, Types.or(Primitives.CHAR_SEQUENCE, Types.primitive("{}")))
            .build());
    }
}
