package moe.wolfgirl.probejs.features.rhizo;

import dev.latvian.mods.rhino.util.remapper.RemapperManager;
import lombok.val;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author ZZZank
 */
public final class RemapperBridge {

    private static final Function<Class<?>, String> remapClassFn;
    private static final Function<String, String> unmapClassFn;
    private static final BiFunction<Class<?>, Field, String> remapFieldFn;
    private static final BiFunction<Class<?>, Method, String> remapMethodFn;

    static {
        if (RhizoState.REMAPPER) {
            val remapper = RemapperManager.getDefault();
            remapClassFn = remapper::remapClass;
            unmapClassFn = remapper::unmapClass;
            remapFieldFn = remapper::remapField;
            remapMethodFn = remapper::remapMethod;
        } else {
            remapClassFn = Class::getName;
            unmapClassFn = Function.identity();
            remapFieldFn = (c, f) -> f.getName();
            remapMethodFn = (c, m) -> m.getName();
        }
    }

    public static String remapClass(Class<?> from) {
        val remapped = remapClassFn.apply(from);
        return remapped.isEmpty() ? from.getName() : remapped;
    }

    public static String unmapClass(String from) {
        val remapped = unmapClassFn.apply(from);
        return remapped.isEmpty() ? from : remapped;
    }

    public static String remapField(Class<?> from, Field field) {
        val remapped = remapFieldFn.apply(from, field);
        return remapped.isEmpty() ? field.getName() : remapped;
    }

    public static String remapMethod(Class<?> from, Method method) {
        val remapped = remapMethodFn.apply(from, method);
        return remapped.isEmpty() ? method.getName() : remapped;
    }

    private RemapperBridge() {}
}
