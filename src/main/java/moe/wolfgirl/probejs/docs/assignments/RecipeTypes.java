package moe.wolfgirl.probejs.docs.assignments;


import dev.latvian.kubejs.recipe.filter.RecipeFilter;
import moe.wolfgirl.probejs.lang.typescript.ScriptDump;
import moe.wolfgirl.probejs.lang.typescript.code.type.Types;
import moe.wolfgirl.probejs.plugin.ProbeJSPlugin;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import java.util.Collections;
import java.util.Set;

public class RecipeTypes extends ProbeJSPlugin {
    @Override
    public void assignType(ScriptDump scriptDump) {

        scriptDump.assignType(ItemLike.class, Types.type(Item.class));

//        scriptDump.assignType(ItemPredicate.class, Types.type(Item.class));
//        scriptDump.assignType(ItemPredicate.class, Types.literal("*"));
//        scriptDump.assignType(ItemPredicate.class, Types.literal("-"));
//        scriptDump.assignType(ItemPredicate.class, Types.lambda()
//                .param("item", Types.type(ItemStack.class))
//                .returnType(Types.BOOLEAN)
//                .build());

//        scriptDump.assignType(SizedIngredient.class, Types.type(ItemStack.class));

        scriptDump.assignType(RecipeFilter.class, Types.primitive("RegExp"));
        scriptDump.assignType(RecipeFilter.class, Types.literal("*"));
        scriptDump.assignType(RecipeFilter.class, Types.literal("-"));
        scriptDump.assignType(RecipeFilter.class, Types.type(RecipeFilter.class).asArray());

        scriptDump.assignType(RecipeFilter.class, "RecipeFilterObject", Types.object()
                .member("or", true, Types.type(RecipeFilter.class))
                .member("not", true, Types.type(RecipeFilter.class))
                .member("id", true, Types.primitive("Special.RecipeId"))
                .member("type", true, Types.primitive("Special.RecipeType"))
                .member("group", true, Types.STRING)
                .member("mod", true, Types.primitive("Special.Mod"))
                .member("input", true, Types.type(Ingredient.class))
                .member("output", true, Types.type(ItemStack.class))
                .build());

        // Note that this is fluid ingredient without amount
//        scriptDump.assignType(FluidIngredientJS.class, Types.type(Fluid.class));
//        scriptDump.assignType(FluidIngredientJS.class, Types.primitive("`#${Special.FluidTag}`"));
//        scriptDump.assignType(FluidIngredientJS.class, Types.primitive("`@${Special.Mod}`"));
//        scriptDump.assignType(FluidIngredientJS.class, Types.primitive("RegExp"));
    }

    @Override
    public void addGlobals(ScriptDump scriptDump) {
        super.addGlobals(scriptDump);
    }

    @Override
    public Set<Class<?>> provideJavaClass(ScriptDump scriptDump) {
        return Collections.singleton(RecipeFilter.class);
    }
}
