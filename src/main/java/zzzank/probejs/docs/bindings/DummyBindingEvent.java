package zzzank.probejs.docs.bindings;

import dev.latvian.kubejs.script.BindingsEvent;
import dev.latvian.kubejs.script.ScriptManager;
import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Scriptable;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ZZZank
 */
class DummyBindingEvent extends BindingsEvent {

    final Map<String, Object> constants = new HashMap<>();
    final Map<String, Class<?>> classes = new HashMap<>();
    final Map<String, BaseFunction> functions = new HashMap<>();

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
