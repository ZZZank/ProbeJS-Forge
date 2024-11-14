package zzzank.probejs.docs.events;

import dev.latvian.kubejs.recipe.RecipeEventJS;
import dev.latvian.kubejs.recipe.RecipeFunction;
import dev.latvian.kubejs.script.ScriptType;
import lombok.val;
import net.minecraft.resources.ResourceLocation;
import zzzank.probejs.ProbeJS;
import zzzank.probejs.docs.recipes.RecipeEventReader;
import zzzank.probejs.lang.java.clazz.ClassPath;
import zzzank.probejs.lang.transpiler.transformation.InjectBeans;
import zzzank.probejs.lang.typescript.ScriptDump;
import zzzank.probejs.lang.typescript.TypeScriptFile;
import zzzank.probejs.lang.typescript.code.member.ClassDecl;
import zzzank.probejs.lang.typescript.code.member.TypeDecl;
import zzzank.probejs.lang.typescript.code.type.Types;
import zzzank.probejs.lang.typescript.code.type.js.JSLambdaType;
import zzzank.probejs.lang.typescript.refer.ImportInfo;
import zzzank.probejs.plugin.ProbeJSPlugin;
import zzzank.probejs.plugin.ProbeJSPlugins;

import java.util.*;

public class RecipeEvents extends ProbeJSPlugin {

    private static Map<String, Object> capturedRecipes;

    public static void captureRecipes(Map<String, Object> recipes) {
        capturedRecipes = recipes;
    }

    public static final Map<String, ResourceLocation> SHORTCUTS = new HashMap<>();
    public static final String PATH_BASE = "zzzank.probejs.generated.recipes";
    public static final String NAME_DOCUMENTED = "DocumentedRecipes";
    public static final ClassPath PATH_DOCUMENTED = ClassPath.fromRaw(PATH_BASE + "." + NAME_DOCUMENTED);

    static {
        SHORTCUTS.put("shaped", new ResourceLocation("kubejs", "shaped"));
        SHORTCUTS.put("shapeless", new ResourceLocation("kubejs", "shapeless"));
        SHORTCUTS.put("smelting", new ResourceLocation("minecraft", "smelting"));
        SHORTCUTS.put("blasting", new ResourceLocation("minecraft", "blasting"));
        SHORTCUTS.put("smoking", new ResourceLocation("minecraft", "smoking"));
        SHORTCUTS.put("campfireCooking", new ResourceLocation("minecraft", "campfire_cooking"));
        SHORTCUTS.put("stonecutting", new ResourceLocation("minecraft", "stonecutting"));
        SHORTCUTS.put("smithing", new ResourceLocation("minecraft", "smithing"));
    }

    private Map<ResourceLocation, JSLambdaType> getPredefinedRecipeDocs(ScriptDump scriptDump) {
        val pred = new HashMap<ResourceLocation, JSLambdaType>();
        ProbeJSPlugins.forEachPlugin(p -> p.addPredefinedRecipeDoc(scriptDump, pred));
        ProbeJS.LOGGER.debug("Read {} predefined recipe docs", pred.size());
        return pred;
    }

    @Override
    public void modifyClasses(ScriptDump scriptDump, Map<ClassPath, TypeScriptFile> globalClasses) {
        if (scriptDump.scriptType != ScriptType.SERVER && scriptDump.scriptType != ScriptType.STARTUP) {
            return;
        }

        //1.Generate recipe schema classes
        // Also generate the documented recipe class containing all stuffs from everywhere

        val reader = new RecipeEventReader(scriptDump.transpiler.typeConverter, getPredefinedRecipeDocs(scriptDump));
        reader.read(capturedRecipes);
        val parsed = reader.result.build();

        val documentFile = new TypeScriptFile(PATH_DOCUMENTED);
        documentFile.addCode(new TypeDecl(NAME_DOCUMENTED, parsed));
        globalClasses.put(PATH_DOCUMENTED, documentFile);

        //2.Inject types into the RecipeEventJS
        val recipeEventFile = globalClasses.get(ClassPath.fromJava(RecipeEventJS.class));
        val recipeEvent = recipeEventFile.findCode(ClassDecl.class).orElse(null);
        if (recipeEvent == null) {
            ProbeJS.LOGGER.error("RecipeEventJS class declaration not found");
            return; // What???
        }
        for (val m : recipeEvent.methods) {
            if (m.params.isEmpty() && m.name.equals("getRecipes")) {
                m.returnType = Types.type(PATH_DOCUMENTED);
                break;
            }
        }
        for (val code : recipeEvent.bodyCode) {
            if (code instanceof InjectBeans.BeanDecl beanDecl && beanDecl.name.equals("recipes")) {
                beanDecl.baseType = Types.type(PATH_DOCUMENTED);
                break;
            }
        }
        recipeEventFile.declaration.addImport(ImportInfo.ofOriginal(PATH_DOCUMENTED));

        //3.Make shortcuts valid recipe functions
        for (val field : recipeEvent.fields) {
            val parts = SHORTCUTS.get(field.name);
            if (parts == null) {
                continue;
            }

            field.type = Types.primitive(
                String.format(
                    "%s[%s][%s]",
                    NAME_DOCUMENTED,
                    ProbeJS.GSON.toJson(parts.getNamespace()),
                    ProbeJS.GSON.toJson(parts.getPath())
                )
            );

            for (val info : field.type.getImportInfos()) {
                recipeEventFile.declaration.addImport(info);
            }
        }
    }

    @Override
    public Set<Class<?>> provideJavaClass(ScriptDump scriptDump) {
        if (scriptDump.scriptType == ScriptType.CLIENT) {
            return Collections.emptySet();
        }

        val jClasses = collectClasses(capturedRecipes);
        //make sure RecipeEventJS has TSFile generated, to prevent modifyClasses from failing
        jClasses.add(RecipeEventJS.class);
        return jClasses;
    }

    private static Set<Class<?>> collectClasses(Map<?, ?> recipes) {
        val collected = new HashSet<Class<?>>();
        for (val value : recipes.values()) {
            if (value instanceof RecipeFunction rFn) {
                collected.add(rFn.type.getClass());
                collected.add(rFn.type.factory.get().getClass());
            } else if (value instanceof Map<?, ?> m) {
                collected.addAll(collectClasses(m));
            }
        }
        return collected;
    }
}
