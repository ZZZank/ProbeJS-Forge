package moe.wolfgirl.probejs.utils;

import com.probejs.ProbeJS;
import com.probejs.util.PUtil;
import dev.latvian.mods.rhino.Context;
import lombok.val;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author ZZZank
 */
public abstract class RemapperBridge {

    private static boolean valid = false;
    private static Object source;
    private static Method mapClass;
    private static Method unmapClass;
    private static Method mapField;
    private static Method mapMethod;

    public static void refreshRemapper() {
        if (!ProbeJS.isRhizoLoaded()) {
            ProbeJS.LOGGER.warn("You seem to be using Rhino instead of newer Rhizo, skipping Remapper check");
            valid = false;
            return;
        }
        ProbeJS.LOGGER.info("Refreshing Remapper reference");
        try {
            val c = Class.forName("dev.latvian.mods.rhino.util.remapper.RemapperManager");
            source = Context.class.getMethod("getDefault").invoke(null);
            mapClass = c.getMethod("remapClass", Class.class);
            unmapClass = c.getMethod("unmapClass", String.class);
            mapField = c.getMethod("remapField", Class.class, Field.class);
            mapMethod = c.getMethod("remapMethod", Class.class, Method.class);

            ProbeJS.LOGGER.info("Remapper reference refreshed");
            valid = true;
        } catch (Exception e) {
            ProbeJS.LOGGER.error("Unable to refresh Remapper reference");
            valid = false;
            e.printStackTrace();
        }
    }

    public static String remapClassOrDefault(Class<?> from) {
        val original = from.getName();
        if (!valid) {
            return original;
        }
        return PUtil.tryOrDefault(() -> (String) mapClass.invoke(source, from), original);
    }

    public static String unmapClassOrDefault(String from) {
        val original = from;
        if (!valid) {
            return original;
        }
        return PUtil.tryOrDefault(() -> (String) unmapClass.invoke(source, from), original);
    }

    public static String remapFieldOrDefault(Class<?> from, Field field) {
        val original = field.getName();
        if (!valid) {
            return original;
        }
        return PUtil.tryOrDefault(() -> (String) mapField.invoke(source, from, field), original);
    }

    public static String remapMethodOrDefault(Class<?> from, Method method) {
        val original = method.getName();
        if (!valid) {
            return original;
        }
        return PUtil.tryOrDefault(() -> (String) mapMethod.invoke(source, from, method), original);
    }
}
