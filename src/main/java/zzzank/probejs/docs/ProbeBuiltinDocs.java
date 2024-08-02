package zzzank.probejs.docs;

import net.minecraft.resources.ResourceLocation;
import zzzank.probejs.ProbeJS;
import zzzank.probejs.docs.assignments.*;
import zzzank.probejs.docs.events.ForgeEvents;
import zzzank.probejs.docs.events.KubeEvents;
import zzzank.probejs.docs.events.RecipeEvents;
import zzzank.probejs.docs.recipes.BuiltinRecipeDocs;
import zzzank.probejs.lang.java.clazz.ClassPath;
import zzzank.probejs.lang.schema.SchemaDump;
import zzzank.probejs.lang.snippet.SnippetDump;
import zzzank.probejs.lang.transpiler.Transpiler;
import zzzank.probejs.lang.transpiler.TypeConverter;
import zzzank.probejs.lang.typescript.ScriptDump;
import zzzank.probejs.lang.typescript.TypeScriptFile;
import zzzank.probejs.lang.typescript.code.type.BaseType;
import zzzank.probejs.plugin.ProbeJSPlugin;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Delegate calls to a set of internal ProbeJSPlugin to separate different
 * features
 */
public class ProbeBuiltinDocs extends ProbeJSPlugin {
    public static final ProbeBuiltinDocs INSTANCE = new ProbeBuiltinDocs();

    // So docs can be added stateless
    public final static List<Supplier<ProbeJSPlugin>> BUILTIN_DOCS = new ArrayList<>(Arrays.asList(
        //type
        RegistryTypes::new,
        SpecialTypes::new,
        Primitives::new,
        JavaPrimitives::new,
        RecipeTypes::new,
        WorldTypes::new,
        EnumTypes::new,
        KubeWrappers::new,
        FunctionalInterfaces::new,
        //binding
        Bindings::new,
        //event
        KubeEvents::new,
//            TagEvents::new,
        RecipeEvents::new,
        BuiltinRecipeDocs::new,
//            RegistryEvents::new,
        ForgeEvents::new,
        //misc
        ParamFix::new,
        Snippets::new
    ));

    private static void forEach(Consumer<ProbeJSPlugin> consumer) {
        for (Supplier<ProbeJSPlugin> builtinDoc : BUILTIN_DOCS) {
            try {
                consumer.accept(builtinDoc.get());
            } catch (Throwable t) {
                ProbeJS.LOGGER.error(String.format("Error when applying builtin doc: %s", builtinDoc.get().getClass()));
                ProbeJS.LOGGER.error(t.getMessage());
                for (StackTraceElement stackTraceElement : t.getStackTrace()) {
                    ProbeJS.LOGGER.error(stackTraceElement.toString());
                }
                ProbeJS.LOGGER.error("If you found any problem in generated docs, please report to ProbeJS's github!");
            }
        }
    }

    @Override
    public void addGlobals(ScriptDump scriptDump) {
        forEach(builtinDoc -> builtinDoc.addGlobals(scriptDump));
    }

    @Override
    public void modifyClasses(ScriptDump scriptDump, Map<ClassPath, TypeScriptFile> globalClasses) {
        forEach(builtinDoc -> builtinDoc.modifyClasses(scriptDump, globalClasses));
    }

    @Override
    public void assignType(ScriptDump scriptDump) {
        forEach(builtinDoc -> builtinDoc.assignType(scriptDump));
    }

    @Override
    public void addPredefinedTypes(TypeConverter converter) {
        forEach(builtinDoc -> builtinDoc.addPredefinedTypes(converter));
    }

    @Override
    public void denyTypes(Transpiler transpiler) {
        forEach(builtinDoc -> builtinDoc.denyTypes(transpiler));
    }

    @Override
    public Set<Class<?>> provideJavaClass(ScriptDump scriptDump) {
        Set<Class<?>> allClasses = new HashSet<>();
        forEach(builtinDoc -> allClasses.addAll(builtinDoc.provideJavaClass(scriptDump)));
        return allClasses;
    }

    @Override
    public void addVSCodeSnippets(SnippetDump dump) {
        forEach(builtinDoc -> builtinDoc.addVSCodeSnippets(dump));
    }

    @Override
    public void addJsonSchema(SchemaDump dump) {
        forEach(builtinDoc -> builtinDoc.addJsonSchema(dump));
    }

    @Override
    public void addPredefinedRecipeDoc(ScriptDump scriptDump, Map<ResourceLocation, BaseType> predefined) {
        forEach(builtinDoc -> builtinDoc.addPredefinedRecipeDoc(scriptDump, predefined));
    }
}
