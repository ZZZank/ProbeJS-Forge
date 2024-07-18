package moe.wolfgirl.probejs.docs;

import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.NativeJavaClass;
import dev.latvian.mods.rhino.Scriptable;
import lombok.val;
import moe.wolfgirl.probejs.lang.transpiler.TypeConverter;
import moe.wolfgirl.probejs.lang.typescript.ScriptDump;
import moe.wolfgirl.probejs.lang.typescript.code.Code;
import moe.wolfgirl.probejs.lang.typescript.code.ts.ReexportDeclaration;
import moe.wolfgirl.probejs.lang.typescript.code.ts.VariableDeclaration;
import moe.wolfgirl.probejs.lang.typescript.code.type.BaseType;
import moe.wolfgirl.probejs.lang.typescript.code.type.Types;
import moe.wolfgirl.probejs.plugin.ProbeJSPlugin;

import java.util.*;

/**
 * Adds bindings to some stuffs...
 */
public class Bindings extends ProbeJSPlugin {
    @Override
    public void addGlobals(ScriptDump scriptDump) {
        // scans for globally exported objects
        Scriptable scope = scriptDump.attachedScope;

        TypeConverter converter = scriptDump.transpiler.typeConverter;
        Map<String, BaseType> exported = new HashMap<>();
        Map<String, BaseType> reexported = new HashMap<>(); // Namespaces

        for (Object o : scope.getIds()) {
            if (!(o instanceof String id)) {
                continue;
            }
            Object value = scope.get(id, scope);
            if (value instanceof NativeJavaClass javaClass) {
                value = javaClass.getClassObject();
            } else {
                value = Context.jsToJava(value, Object.class);
            }

            if (value instanceof Class<?> c) {
                if (c.isInterface()) {
                    reexported.put(id, converter.convertType(c));
                } else {
                    exported.put(id, Types.typeOf(converter.convertType(c)));
                }
            } else if (!(value instanceof BaseFunction)) {
                exported.put(id, converter.convertType(value.getClass()));
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
        scriptDump.addGlobal("bindings", exported.keySet(), codes.toArray(Code[]::new));
    }

    @Override
    public Set<Class<?>> provideJavaClass(ScriptDump scriptDump) {
        Set<Class<?>> classes = new HashSet<>();
        Scriptable scope = scriptDump.attachedScope;

        for (Object o : scope.getIds()) {
            if (!(o instanceof String id)) {
                continue;
            }
            Object value = scope.get(id, scope);
            if (value instanceof NativeJavaClass javaClass) {
                value = javaClass.getClassObject();
            } else {
                value = Context.jsToJava(value,Object.class);
            }

            if (value instanceof Class<?> c) {
                classes.add(c);
            } else if (!(value instanceof BaseFunction
//                    || value instanceof EventGroupWrapper
            )) {
                // No base function as don't know how to get type info
                // No events because they will be dumped separately
                classes.add(value.getClass());
            }
        }
        return classes;
    }
}
