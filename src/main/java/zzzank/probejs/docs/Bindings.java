package zzzank.probejs.docs;

import dev.latvian.kubejs.script.BindingsEvent;
import dev.latvian.kubejs.script.ScriptManager;
import dev.latvian.kubejs.script.TypedDynamicFunction;
import dev.latvian.kubejs.util.KubeJSPlugins;
import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.NativeJavaClass;
import dev.latvian.mods.rhino.Scriptable;
import lombok.val;
import zzzank.probejs.features.kubejs.BindingFilter;
import zzzank.probejs.lang.transpiler.TypeConverter;
import zzzank.probejs.lang.typescript.ScriptDump;
import zzzank.probejs.lang.typescript.code.Code;
import zzzank.probejs.lang.typescript.code.ts.ReexportDeclaration;
import zzzank.probejs.lang.typescript.code.ts.VariableDeclaration;
import zzzank.probejs.lang.typescript.code.type.BaseType;
import zzzank.probejs.lang.typescript.code.type.Types;
import zzzank.probejs.mixins.AccessTypedDynamicFunction;
import zzzank.probejs.plugin.ProbeJSPlugin;

import java.util.*;

/**
 * Adds bindings to some stuffs...
 */
public class Bindings extends ProbeJSPlugin {

    private static class DummyBindingEvent extends BindingsEvent {

        private final Map<String, Object> constants = new HashMap<>();
        private final Map<String, Class<?>> classes = new HashMap<>();
        private final Map<String, BaseFunction> functions = new HashMap<>();

        public DummyBindingEvent(ScriptManager m, Context cx, Scriptable s) {
            super(m, cx, s);
        }

        @Override
        public void add(String name, Object value) {
            if (value instanceof Class<?> c) {
                classes.put(name, c);
            } else if (value instanceof BaseFunction fn) {
                functions.put(name, fn);
            } else {
                constants.put(name, value);
            }
        }
    }

    @Override
    public void addGlobals(ScriptDump scriptDump) {

        val event = new DummyBindingEvent(scriptDump.manager, scriptDump.attachedContext, scriptDump.attachedScope);
        KubeJSPlugins.forEachPlugin(plugin -> plugin.addBindings(event));

        val filter = new BindingFilter();
        ProbeJSPlugin.forEachPlugin(plugin -> plugin.denyBindings(filter));

        TypeConverter converter = scriptDump.transpiler.typeConverter;
        Map<String, BaseType> exported = new HashMap<>();
        Map<String, BaseType> reexported = new HashMap<>(); // Namespaces

        for (val entry : event.functions.entrySet()) {
            val name = entry.getKey();
            if (filter.isFunctionDenied(name)) {
                continue;
            }
            val fn = Types.lambda().returnType(Types.ANY);
            if (entry.getValue() instanceof TypedDynamicFunction typed) {
                val types = ((AccessTypedDynamicFunction) typed).types();
                for (int i = 0; i < types.length; i++) {
                    Class<?> type = types[i];
                    fn.param("arg" + i, type == null ? Types.ANY : Types.typeMaybeGeneric(type));
                }
            } else {
                fn.param("args", Types.ANY.asArray(), false, true);
            }
            exported.put(name, fn.build());
        }

        for (val entry : event.constants.entrySet()) {
            val name = entry.getKey();
            if (filter.isConstantDenied(name)) {
                continue;
            }
            val obj = entry.getValue();
            exported.put(name, converter.convertType(obj.getClass()));
        }

        for (val entry : event.classes.entrySet()) {
            val id = entry.getKey();
            if (filter.isClassDenied(id)) {
                continue;
            }
            val c = entry.getValue();
            if (c.isInterface()) {
                reexported.put(id, converter.convertType(c));
            } else {
                exported.put(id, Types.typeOf(converter.convertType(c)));
            }
        }

        List<Code> codes = new ArrayList<>();
        for (val entry : exported.entrySet()) {
            val symbol = entry.getKey();
            val type = entry.getValue();
            codes.add(new VariableDeclaration(symbol, type));
        }
        for (val entry : reexported.entrySet()) {
            val symbol = entry.getKey();
            val type = entry.getValue();
            codes.add(new ReexportDeclaration(symbol, type));
        }
        scriptDump.addGlobal("bindings", exported.keySet(), codes.toArray(new Code[0]));
    }

    @Override
    public Set<Class<?>> provideJavaClass(ScriptDump scriptDump) {

        val event = new DummyBindingEvent(scriptDump.manager, scriptDump.attachedContext, scriptDump.attachedScope);
        KubeJSPlugins.forEachPlugin(plugin -> plugin.addBindings(event));

        Set<Class<?>> classes = new HashSet<>(event.classes.values());
        for (Object o : event.constants.values()) {
            if (o instanceof NativeJavaClass njc) {
                classes.add(njc.getClassObject());
            } else if (o instanceof Class<?> c) {
                classes.add(c);
            } else {
                classes.add(o.getClass());
            }
        }
        for (BaseFunction fn : event.functions.values()) {
            if (!(fn instanceof TypedDynamicFunction typed)) {
                continue;
            }
            for (Class<?> type : ((AccessTypedDynamicFunction) typed).types()) {
                if (type != null) {
                    classes.add(type);
                }
            }
        }

        return classes;
    }
}
