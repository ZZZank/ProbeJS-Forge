package zzzank.probejs.docs.recipes;

import lombok.val;
import net.minecraft.resources.ResourceLocation;
import zzzank.probejs.lang.typescript.ScriptDump;
import zzzank.probejs.lang.typescript.code.type.TSClassType;
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

    public static final List<Supplier<ProbeJSPlugin>> ALL = new ArrayList<>(Arrays.asList(
        Minecraft::new,
        ArsNouveau::new,
        Thermal::new,
        KubeJS::new
    ));

    public static JSLambdaType.Builder recipeFn() {
        return Types.lambda().method();
    }

    public static TSClassType classType(String className) {
        try {
            val c = Class.forName(className);
            return Types.type(c);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addPredefinedRecipeDoc(ScriptDump scriptDump, Map<ResourceLocation, JSLambdaType> predefined) {
        for (Supplier<ProbeJSPlugin> supplier : ALL) {
            supplier.get().addPredefinedRecipeDoc(scriptDump, predefined);
        }
    }
}
