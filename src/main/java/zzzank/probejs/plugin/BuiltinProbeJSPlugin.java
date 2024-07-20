package zzzank.probejs.plugin;

import dev.latvian.kubejs.script.BindingsEvent;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.util.ClassFilter;
import lombok.val;
import net.minecraft.resources.ResourceLocation;
import zzzank.probejs.ProbeJS;
import zzzank.probejs.docs.ProbeBuiltinDocs;
import zzzank.probejs.events.ProbeEvents;
import zzzank.probejs.events.SnippetGenerationEventJS;
import zzzank.probejs.events.TypeAssignmentEventJS;
import zzzank.probejs.events.TypingModificationEventJS;
import zzzank.probejs.lang.java.clazz.ClassPath;
import zzzank.probejs.lang.schema.SchemaDump;
import zzzank.probejs.lang.snippet.SnippetDump;
import zzzank.probejs.lang.transpiler.Transpiler;
import zzzank.probejs.lang.transpiler.TypeConverter;
import zzzank.probejs.lang.typescript.ScriptDump;
import zzzank.probejs.lang.typescript.TypeScriptFile;
import zzzank.probejs.lang.typescript.code.type.Types;
import zzzank.probejs.lang.typescript.code.type.js.JSLambdaType;

import java.util.Map;
import java.util.Set;

public class BuiltinProbeJSPlugin extends ProbeJSPlugin {
//    @Override
//    public void registerEvents() {
//        ProbeEvents.GROUP.register();
//    }

    @Override
    public void addBindings(BindingsEvent event) {
        readFromBindings(event);
        if (event.manager.type == ScriptType.CLIENT) {
            event.add("Types", Types.class);
        }
        event.add("require", new Require(event.manager));
    }

    private static void readFromBindings(BindingsEvent event) {
        ProbeJS.LOGGER.debug("read binding infos for script type {}", event.type.name);
        val dump = ScriptDump.forType(event.type).get();
        //a bad idea, because KubeJS 1.16 will call BindingsEvent for EVERY script pack
        dump.attachedContext = event.context;
        dump.attachedScope = event.scope;
    }

    @Override
    public void addClasses(ScriptType type, ClassFilter filter) {
        // lol
        filter.deny("org.jetbrains.java.decompiler");
        filter.deny("com.github.javaparser");
        filter.deny("org.java_websocket");
    }

    @Override
    public void assignType(ScriptDump scriptDump) {
        ProbeBuiltinDocs.INSTANCE.assignType(scriptDump);
        new TypeAssignmentEventJS(scriptDump).post(ScriptType.CLIENT, ProbeEvents.ASSIGN_TYPE);
    }

    @Override
    public void modifyClasses(ScriptDump scriptDump, Map<ClassPath, TypeScriptFile> globalClasses) {
        ProbeBuiltinDocs.INSTANCE.modifyClasses(scriptDump, globalClasses);
        new TypingModificationEventJS(scriptDump, globalClasses).post(ScriptType.CLIENT, ProbeEvents.MODIFY_DOC);
    }

    @Override
    public void addGlobals(ScriptDump scriptDump) {
        ProbeBuiltinDocs.INSTANCE.addGlobals(scriptDump);
    }

    @Override
    public void addPredefinedTypes(TypeConverter converter) {
        ProbeBuiltinDocs.INSTANCE.addPredefinedTypes(converter);
    }

    @Override
    public void denyTypes(Transpiler transpiler) {
        ProbeBuiltinDocs.INSTANCE.denyTypes(transpiler);

        transpiler.reject(Object.class);

        transpiler.reject(String.class);
        transpiler.reject(Character.class);
        transpiler.reject(Character.TYPE);

        transpiler.reject(Void.class);
        transpiler.reject(Void.TYPE);

        transpiler.reject(Long.class);
        transpiler.reject(Long.TYPE);
        transpiler.reject(Integer.class);
        transpiler.reject(Integer.TYPE);
        transpiler.reject(Short.class);
        transpiler.reject(Short.TYPE);
        transpiler.reject(Byte.class);
        transpiler.reject(Byte.TYPE);
        transpiler.reject(Number.class);
        transpiler.reject(Double.class);
        transpiler.reject(Double.TYPE);
        transpiler.reject(Float.class);
        transpiler.reject(Float.TYPE);

        transpiler.reject(Boolean.class);
        transpiler.reject(Boolean.TYPE);
    }

    @Override
    public Set<Class<?>> provideJavaClass(ScriptDump scriptDump) {
        return ProbeBuiltinDocs.INSTANCE.provideJavaClass(scriptDump);
    }

    @Override
    public void addVSCodeSnippets(SnippetDump dump) {
        ProbeBuiltinDocs.INSTANCE.addVSCodeSnippets(dump);
        new SnippetGenerationEventJS(dump).post(ScriptType.CLIENT, ProbeEvents.SNIPPETS);
    }

    @Override
    public void addJsonSchema(SchemaDump dump) {
        ProbeBuiltinDocs.INSTANCE.addJsonSchema(dump);
    }

    @Override
    public void addPredefinedRecipeDoc(ScriptDump scriptDump, Map<ResourceLocation, JSLambdaType> predefined) {
        ProbeBuiltinDocs.INSTANCE.addPredefinedRecipeDoc(scriptDump, predefined);
    }
}
