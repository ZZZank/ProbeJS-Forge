package zzzank.probejs.docs.bindings;

import dev.latvian.kubejs.script.TypedDynamicFunction;
import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.NativeJavaClass;
import lombok.val;
import zzzank.probejs.ProbeConfig;
import zzzank.probejs.features.kubejs.BindingFilter;
import zzzank.probejs.lang.transpiler.TypeConverter;
import zzzank.probejs.lang.typescript.ScriptDump;
import zzzank.probejs.lang.typescript.code.Code;
import zzzank.probejs.lang.typescript.code.ts.ReexportDeclaration;
import zzzank.probejs.lang.typescript.code.ts.VariableDeclaration;
import zzzank.probejs.lang.typescript.code.type.BaseType;
import zzzank.probejs.lang.typescript.code.type.Types;
import zzzank.probejs.mixins.AccessTypedDynamicFunction;
import zzzank.probejs.mixins.patch.MixinNativeJavaObject;
import zzzank.probejs.plugin.ProbeJSPlugin;
import zzzank.probejs.plugin.ProbeJSPlugins;

import java.util.*;

/**
 * Adds bindings to some stuffs...
 */
public class Bindings extends ProbeJSPlugin {

    private final Map<String, Object> constants = new HashMap<>();
    private final Map<String, Class<?>> classes = new HashMap<>();
    private final Map<String, BaseFunction> functions = new HashMap<>();

    @Override
    public void addGlobals(ScriptDump scriptDump) {
        refreshBindings(scriptDump);

        val filter = new BindingFilter();
        ProbeJSPlugins.forEachPlugin(plugin -> plugin.denyBindings(filter));

        TypeConverter converter = scriptDump.transpiler.typeConverter;
        Map<String, BaseType> exported = new HashMap<>();
        Map<String, BaseType> reexported = new HashMap<>(); // Namespaces

        for (val entry : functions.entrySet()) {
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

        for (val entry : constants.entrySet()) {
            val name = entry.getKey();
            if (filter.isConstantDenied(name)) {
                continue;
            }
            val obj = entry.getValue();
            exported.put(name, converter.convertType(obj.getClass()));
        }

        for (val entry : classes.entrySet()) {
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

        if (ProbeConfig.resolveGlobal.get()) {
            val resolveGlobal = new ResolveGlobal(constants);
            resolveGlobal.addGlobals(scriptDump);
            exported.put(ResolveGlobal.NAME, exported.get(ResolveGlobal.NAME).and(ResolveGlobal.RESOLVED));
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

    private void clearBindingCache() {
        classes.clear();
        constants.clear();
        functions.clear();
    }

    private void refreshBindings(ScriptDump scriptDump) {
        clearBindingCache();
        val context = scriptDump.attachedContext;
        val scope = scriptDump.attachedScope;
        for (Object idObj : scope.getIds()) {
            if (!(idObj instanceof String id)) {
                continue;
            }
            var value = scope.get(id, scope);
            if (value instanceof NativeJavaClass nativeJavaClass) {
                value = nativeJavaClass.getClassObject();
            } else {
                value = MixinNativeJavaObject.coerceTypeImpl(
                    context.hasTypeWrappers() ? context.getTypeWrappers() : null,
                    Object.class,
                    value
                );
            }
            if (value instanceof Class<?> c) {
                classes.put(id, c);
            } else if (value instanceof BaseFunction fn) {
                functions.put(id, fn);
            } else {
                constants.put(id, value);
            }
        }
    }

    @Override
    public Set<Class<?>> provideJavaClass(ScriptDump scriptDump) {
        refreshBindings(scriptDump);

        Set<Class<?>> classes = new HashSet<>(this.classes.values());
        for (Object o : constants.values()) {
            if (o instanceof NativeJavaClass njc) {
                classes.add(njc.getClassObject());
            } else if (o instanceof Class<?> c) {
                classes.add(c);
            } else {
                classes.add(o.getClass());
            }
        }
        for (BaseFunction fn : functions.values()) {
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
