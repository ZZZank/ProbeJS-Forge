package zzzank.probejs.plugin;

import dev.latvian.kubejs.KubeJSPlugin;
import dev.latvian.kubejs.script.BindingsEvent;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.util.ClassFilter;
import org.jetbrains.annotations.NotNull;
import zzzank.probejs.events.ProbeEvents;
import zzzank.probejs.events.SnippetGenerationEventJS;
import zzzank.probejs.events.TypeAssignmentEventJS;
import zzzank.probejs.events.TypingModificationEventJS;
import zzzank.probejs.lang.java.clazz.ClassPath;
import zzzank.probejs.lang.snippet.SnippetDump;
import zzzank.probejs.lang.transpiler.Transpiler;
import zzzank.probejs.lang.transpiler.transformation.*;
import zzzank.probejs.lang.typescript.ScriptDump;
import zzzank.probejs.lang.typescript.TypeScriptFile;

import java.util.Map;
import java.util.function.Consumer;

public class BuiltinProbeJSPlugin extends KubeJSPlugin implements ProbeJSPlugin {

    @Override
    public void addBindings(BindingsEvent event) {
        event.add("require", new Require(event.manager));
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
        new TypeAssignmentEventJS(scriptDump).post(ScriptType.CLIENT, ProbeEvents.ASSIGN_TYPE);
    }

    @Override
    public void modifyClasses(ScriptDump scriptDump, Map<ClassPath, TypeScriptFile> globalClasses) {
        new TypingModificationEventJS(scriptDump, globalClasses).post(ScriptType.CLIENT, ProbeEvents.MODIFY_DOC);
    }

    @Override
    public void denyTypes(Transpiler transpiler) {
        transpiler.reject(Object.class);

        transpiler.reject(Character.TYPE);
        transpiler.reject(Void.TYPE);
        transpiler.reject(Long.TYPE);
        transpiler.reject(Integer.TYPE);
        transpiler.reject(Short.TYPE);
        transpiler.reject(Byte.TYPE);
        transpiler.reject(Double.TYPE);
        transpiler.reject(Float.TYPE);
        transpiler.reject(Boolean.TYPE);
    }

    @Override
    public void registerClassTransformer(Consumer<@NotNull ClassTransformer> registration) {
        registration.accept(new InjectAnnotation());
        registration.accept(new InjectArray());
        registration.accept(new InjectBeans());
        registration.accept(new InjectSpecialType());
    }

    @Override
    public void addVSCodeSnippets(SnippetDump dump) {
        new SnippetGenerationEventJS(dump).post(ScriptType.CLIENT, ProbeEvents.SNIPPETS);
    }
}
