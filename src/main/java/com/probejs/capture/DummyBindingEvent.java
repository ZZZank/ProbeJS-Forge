package com.probejs.capture;

import dev.latvian.kubejs.script.BindingsEvent;
import dev.latvian.kubejs.script.ScriptManager;
import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.ScriptableObject;
import lombok.Getter;
import lombok.val;

import java.util.*;

@Getter
public class DummyBindingEvent extends BindingsEvent {

    private final HashMap<String, Object> constantDumpMap = new HashMap<>();
    private final HashMap<String, Class<?>> classDumpMap = new HashMap<>();
    private final HashMap<String, BaseFunction> functionDump = new HashMap<>();

    public DummyBindingEvent(ScriptManager manager) {
        super(manager, null, null);
    }

    @Override
    public void add(String name, Object value) {
        if (value.getClass() == Class.class) {
            this.classDumpMap.put(name, (Class<?>) value);
        } else if (value instanceof BaseFunction) {
            this.functionDump.put(name, (BaseFunction) value);
        } else {
            this.constantDumpMap.put(name, value);
        }
    }

    public Map<Class<?>, List<String>> getClassDumpReversed() {
        val reversed = new HashMap<Class<?>, List<String>>();
        this.classDumpMap.forEach((name, clazz) -> {
            reversed.computeIfAbsent(clazz, (k) -> new ArrayList<>()).add(name);
        });
        return reversed;
    }

    public Set<Class<?>> getTouchedConstantDump() {
        val touched = new HashSet<Class<?>>();
        for (val constant : getConstantDumpMap().values()) {
            touched.addAll(touchConstantClassRecursive(constant));
        }
        return touched;
    }

    private static Set<Class<?>> touchConstantClassRecursive(Object constantDump) {
        val result = new HashSet<Class<?>>();
        if (constantDump == null) {
            return result;
        }
        if (constantDump instanceof ScriptableObject scriptable) {
            Arrays
                .stream(scriptable.getIds())
                .map(scriptable::get)
                .map(DummyBindingEvent::touchConstantClassRecursive)
                .forEach(result::addAll);
        } else if (constantDump instanceof Map<?, ?> map) {
            map.keySet().stream().map(DummyBindingEvent::touchConstantClassRecursive).forEach(result::addAll);
            map.values().stream().map(DummyBindingEvent::touchConstantClassRecursive).forEach(result::addAll);
        } else if (constantDump instanceof Collection<?> collection) {
            collection.stream().map(DummyBindingEvent::touchConstantClassRecursive).forEach(result::addAll);
        } else {
            result.add(constantDump.getClass());
        }
        return result;
    }
}
