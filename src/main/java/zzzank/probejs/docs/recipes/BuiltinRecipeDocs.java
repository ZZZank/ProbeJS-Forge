package zzzank.probejs.docs.recipes;

import net.minecraft.resources.ResourceLocation;
import zzzank.probejs.lang.typescript.ScriptDump;
import zzzank.probejs.lang.typescript.code.type.Types;
import zzzank.probejs.lang.typescript.code.type.js.JSLambdaType;
import zzzank.probejs.plugin.ProbeJSPlugin;

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
        Minecraft::new,
        Thermal::new,
        KubeJS::new
    ));

    public static JSLambdaType.Builder recipeFn() {
        return Types.lambda().method();
    }

    @Override
    public void addPredefinedRecipeDoc(ScriptDump scriptDump, Map<ResourceLocation, JSLambdaType> predefined) {
        for (Supplier<ProbeJSPlugin> supplier : ALL) {
            supplier.get().addPredefinedRecipeDoc(scriptDump, predefined);
        }
    }
}
