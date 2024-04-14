package com.probejs.util;

import com.probejs.ProbeJS;
import dev.latvian.mods.rhino.Context;
import me.shedaniel.architectury.platform.Platform;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class RemapperBridge {

    public static final RemapperBridge.DummyIRemapper dummyRemapper = new RemapperBridge.DummyIRemapper() {
        @Override
        public String getMappedClass(Class<?> from) {
            return "";
        }

        @Override
        public String getUnmappedClass(String from) {
            return "";
        }

        @Override
        public String getMappedField(Class<?> from, Field field) {
            return "";
        }

        @Override
        public String getMappedMethod(Class<?> from, Method method) {
            return "";
        }
    };
    private static RemapperBridge.DummyIRemapper reference = dummyRemapper;

    public static RemapperBridge.DummyIRemapper getRemapper() {
        return RemapperBridge.reference;
    }

    public static void refreshRemapper() {
        if (!Platform.getMod("rhino").getName().equals("Rhizo")) {
            ProbeJS.LOGGER.warn("The game seems to be using Rhino instead of newer Rhizo, skipping Remapper check");
            return;
        }
        ProbeJS.LOGGER.info("Refreshing Remapper reference");
        try {
            //TODO: correct class type
            Class<?> c = Class.forName("dev.latvian.mods.rhino.util.remapper.Remapper");
            RemapperBridge.reference = new ReflectingRemapper(c);
            ProbeJS.LOGGER.info("Remapper reference refreshed");
        } catch (Exception e) {
            ProbeJS.LOGGER.error("Unable to refresh Remapper reference");
            e.printStackTrace();
            RemapperBridge.reference = RemapperBridge.dummyRemapper;
        }
    }

    public interface DummyIRemapper {

        String getMappedClass(Class<?> from);

        String getUnmappedClass(String from);

        String getMappedField(Class<?> from, Field field);

        String getMappedMethod(Class<?> from, Method method);

    }

    private static class ReflectingRemapper implements DummyIRemapper {
        private final Object source;
        private final Method mapClass;
        private final Method unmapClass;
        private final Method mapField;
        private final Method mapMethod;

        public ReflectingRemapper(Class<?> c) throws Exception {
            source = Context.class.getMethod("getRemapper").invoke(null);
            mapClass = c.getMethod("getMappedClass", Class.class);
            unmapClass = c.getMethod("getUnmappedClass", String.class);
            mapField = c.getMethod("getMappedField", Class.class, Field.class);
            mapMethod = c.getMethod("getMappedMethod", Class.class, Method.class);
        }

        @Override
        public String getMappedClass(Class<?> from) {
            return PUtil.tryOrDefault(()->(String) mapClass.invoke(source, from), "");
        }

        @Override
        public String getUnmappedClass(String from) {
            return PUtil.tryOrDefault(()->(String) unmapClass.invoke(source, from), "");
        }

        @Override
        public String getMappedField(Class<?> from, Field field) {
            return PUtil.tryOrDefault(()->(String) mapField.invoke(source, from, field), "");
        }

        @Override
        public String getMappedMethod(Class<?> from, Method method) {
            return PUtil.tryOrDefault(()->(String) mapMethod.invoke(source, from, method), "");
        }
    }
}
