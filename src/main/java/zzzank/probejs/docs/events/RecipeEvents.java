package zzzank.probejs.docs.events;

import dev.latvian.kubejs.recipe.RecipeEventJS;
import dev.latvian.kubejs.recipe.RecipeTypeJS;
import dev.latvian.kubejs.recipe.RegisterRecipeHandlersEvent;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.util.KubeJSPlugins;
import lombok.val;
import net.minecraft.resources.ResourceLocation;
import zzzank.probejs.ProbeJS;
import zzzank.probejs.lang.java.clazz.ClassPath;
import zzzank.probejs.lang.transpiler.transformation.InjectBeans;
import zzzank.probejs.lang.typescript.ScriptDump;
import zzzank.probejs.lang.typescript.TypeScriptFile;
import zzzank.probejs.lang.typescript.code.member.ClassDecl;
import zzzank.probejs.lang.typescript.code.ts.Statements;
import zzzank.probejs.lang.typescript.code.type.BaseType;
import zzzank.probejs.lang.typescript.code.type.Types;
import zzzank.probejs.lang.typescript.code.type.js.JSLambdaType;
import zzzank.probejs.plugin.ProbeJSPlugin;
import zzzank.probejs.plugin.ProbeJSPlugins;

import java.util.*;

public class RecipeEvents extends ProbeJSPlugin {

    public static final Map<String, ResourceLocation> SHORTCUTS = new HashMap<>();
    public static final String PATH_BASE = "zzzank.probejs.generated.recipes";
    public static final ClassPath DOCUMENTED = new ClassPath(PATH_BASE + ".DocumentedRecipes");

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

    public final Map<ResourceLocation, RecipeTypeJS> ALL = new HashMap<>();

    private Map<String, Map<String, BaseType>> getGroupedRecipeTypes(ScriptDump scriptDump) {
        val converter = scriptDump.transpiler.typeConverter;

        val recipeEvent = new RegisterRecipeHandlersEvent(ALL);
        KubeJSPlugins.forEachPlugin(plugin -> plugin.addRecipes(recipeEvent));

        val predefinedTypes = getPredefinedRecipeDocs(scriptDump);

        val grouped = new HashMap<String, Map<String, BaseType>>();
        for (val entry : ALL.entrySet()) {
            val resLocation = entry.getKey();
            var recipeFn = predefinedTypes.get(resLocation);
            if (recipeFn == null) {
                val recipeTypeJS = entry.getValue();
                recipeFn = Types
                    .lambda()
                    .param("args", Types.ANY, false, true)
                    .returnType(converter.convertType(recipeTypeJS.factory.get().getClass()))
                    .build();
            }

            grouped
                .computeIfAbsent(resLocation.getNamespace(), k -> new HashMap<>())
                .put(resLocation.getPath(), recipeFn);
        }

        return grouped;
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
        val documentedRecipes = Statements.clazz(DOCUMENTED.getName());

        val grouped = getGroupedRecipeTypes(scriptDump);

        for (val entry : grouped.entrySet()) {
            val namespace = entry.getKey();
            val group = entry.getValue();

            val namespaced = Types.object();
            group.forEach(namespaced::member);

            documentedRecipes.field(namespace, namespaced.build());
        }

        TypeScriptFile documentFile = new TypeScriptFile(DOCUMENTED);
        documentFile.addCode(documentedRecipes.build());
        globalClasses.put(DOCUMENTED, documentFile);

        //2.Inject types into the RecipeEventJS
        val recipeEventFile = globalClasses.get(new ClassPath(RecipeEventJS.class));
        val recipeEvent = recipeEventFile.findCode(ClassDecl.class).orElse(null);
        if (recipeEvent == null) {
            ProbeJS.LOGGER.error("RecipeEventJS class declaration not found");
            return; // What???
        }
        for (val m : recipeEvent.methods) {
            if (m.params.isEmpty() && m.name.equals("getRecipes")) {
                m.returnType = Types.type(DOCUMENTED);
                break;
            }
        }
        for (val code : recipeEvent.bodyCode) {
            if (code instanceof InjectBeans.BeanDecl beanDecl && beanDecl.name.equals("recipes")) {
                beanDecl.baseType = Types.type(DOCUMENTED);
                break;
            }
        }
        recipeEventFile.declaration.addClass(DOCUMENTED);

        //3.Make shortcuts valid recipe functions
        for (val field : recipeEvent.fields) {
            if (!SHORTCUTS.containsKey(field.name)) {
                continue;
            }

            val parts = SHORTCUTS.get(field.name);
            field.type = grouped.get(parts.getNamespace()).get(parts.getPath());

            for (ClassPath usedClassPath : field.type.getUsedClassPaths()) {
                recipeEventFile.declaration.addClass(usedClassPath);
            }
        }
    }

    @Override
    public Set<Class<?>> provideJavaClass(ScriptDump scriptDump) {
        if (scriptDump.scriptType != ScriptType.SERVER) {
            return Collections.emptySet();
        }

        val jClassses = new HashSet<Class<?>>();
        for (RecipeTypeJS recipeTypeJS : ALL.values()) {
            jClassses.add(recipeTypeJS.getClass());
        }
        //make sure RecipeEventJS has TSFile generated, to prevent modifyClasses from failing
        jClassses.add(RecipeEventJS.class);
        return jClassses;
    }
}
