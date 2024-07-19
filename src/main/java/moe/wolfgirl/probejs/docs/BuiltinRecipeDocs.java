package moe.wolfgirl.probejs.docs;

import moe.wolfgirl.probejs.docs.recipes.Minecraft;
import moe.wolfgirl.probejs.lang.typescript.code.type.js.JSObjectType;
import moe.wolfgirl.probejs.plugin.ProbeJSPlugin;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author ZZZank
 */
public class BuiltinRecipeDocs extends ProbeJSPlugin {

    public final List<Supplier<ProbeJSPlugin>> ALL = new ArrayList<>(Arrays.asList(
        Minecraft::new
    ));

    @Override
    public void addPredefinedRecipeDoc(Map<ResourceLocation, JSObjectType> predefined) {
        for (Supplier<ProbeJSPlugin> supplier : ALL) {
            supplier.get().addPredefinedRecipeDoc(predefined);
        }
    }
}
