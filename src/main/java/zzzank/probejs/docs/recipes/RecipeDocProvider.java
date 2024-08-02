package zzzank.probejs.docs.recipes;

import net.minecraft.resources.ResourceLocation;
import zzzank.probejs.lang.typescript.ScriptDump;
import zzzank.probejs.lang.typescript.code.type.BaseType;
import zzzank.probejs.lang.typescript.code.type.Types;
import zzzank.probejs.lang.typescript.code.type.js.JSLambdaType;
import zzzank.probejs.plugin.ProbeJSPlugin;

import java.util.Map;

/**
 * a helper class for easing the usage of recipe doc
 * <p>
 * you need to implement two doc-related methods, {@link RecipeDocProvider#addDocs(ScriptDump)} and {@link RecipeDocProvider#getNamespace()}
 * and a predicate, {@link RecipeDocProvider#shouldEnable()}, used for determining if this recipe doc should be enabled
 * <p>
 * {@link RecipeDocProvider#getNamespace()} should return a valid ResourceLocation namespace, used by {@link RecipeDocProvider#add(String, BaseType)}
 * to generate recipe type id
 * <p>
 * {@link RecipeDocProvider#addDocs(ScriptDump)} is where docs are actually added, you should use {@link RecipeDocProvider#recipeFn()}
 * to get a barebone for your recipe doc, and specify types of params/return, then call {@link JSLambdaType.Builder#build()}
 * and {@link RecipeDocProvider#add(String, BaseType)} to actually add this doc
 *
 * @author ZZZank
 */
public abstract class RecipeDocProvider extends ProbeJSPlugin {

    protected Map<ResourceLocation, BaseType> defined = null;

    public static JSLambdaType.Builder recipeFn() {
        return Types.lambda().methodStyle();
    }

    @Override
    public void addPredefinedRecipeDoc(ScriptDump scriptDump, Map<ResourceLocation, BaseType> predefined) {
        if (!shouldEnable()) {
            return;
        }
        defined = predefined;
        addDocs(scriptDump);
    }

    public abstract void addDocs(ScriptDump scriptDump);

    public void add(String name, BaseType doc) {
        defined.put(new ResourceLocation(getNamespace(), name), doc);
    }

    public abstract String getNamespace();

    public abstract boolean shouldEnable();
}
