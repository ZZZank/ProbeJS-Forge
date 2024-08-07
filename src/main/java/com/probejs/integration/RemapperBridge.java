package com.probejs.integration;

import com.probejs.ProbeJS;
import com.probejs.util.PUtil;
import dev.latvian.mods.rhino.Context;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public abstract class RemapperBridge {

    public static final IRemapper emptyRemapper = new EmptyRemapper();
    private static IRemapper reference = emptyRemapper;

    public static IRemapper getRemapper() {
        return RemapperBridge.reference;
    }

    public static void refreshRemapper() {
        if (!RhizoState.MOD.get()) {
            ProbeJS.LOGGER.warn("You seem to be using Rhino instead of newer Rhizo, skipping Remapper check");
            return;
        }
        ProbeJS.LOGGER.info("Refreshing Remapper reference");
        try {
            RemapperBridge.reference = new ReflectingRemapper();
            ProbeJS.LOGGER.info("Remapper reference refreshed");
        } catch (Exception e) {
            ProbeJS.LOGGER.error("Unable to refresh Remapper reference");
            e.printStackTrace();
            RemapperBridge.reference = RemapperBridge.emptyRemapper;
        }
    }

    public interface IRemapper {

        String remapClass(Class<?> from);

        String unmapClass(String from);

        String remapField(Class<?> from, Field field);

        String remapMethod(Class<?> from, Method method);

    }

    private static class ReflectingRemapper implements IRemapper {
        private final Object source;
        private final Method mapClass;
        private final Method unmapClass;
        private final Method mapField;
        private final Method mapMethod;

        public ReflectingRemapper() throws Exception {
            Class<?> c = Class.forName("dev.latvian.mods.rhino.util.remapper.Remapper");
            source = Context.class.getMethod("getRemapper").invoke(null);
            mapClass = c.getMethod("getMappedClass", Class.class);
            unmapClass = c.getMethod("getUnmappedClass", String.class);
            mapField = c.getMethod("getMappedField", Class.class, Field.class);
            mapMethod = c.getMethod("getMappedMethod", Class.class, Method.class);
        }

        @Override
        public String remapClass(Class<?> from) {
            return PUtil.tryOrDefault(()->(String) mapClass.invoke(source, from), "");
        }

        @Override
        public String unmapClass(String from) {
            return PUtil.tryOrDefault(()->(String) unmapClass.invoke(source, from), "");
        }

        @Override
        public String remapField(Class<?> from, Field field) {
            return PUtil.tryOrDefault(()->(String) mapField.invoke(source, from, field), "");
        }

        @Override
        public String remapMethod(Class<?> from, Method method) {
            return PUtil.tryOrDefault(()->(String) mapMethod.invoke(source, from, method), "");
        }
    }

    private static class EmptyRemapper implements IRemapper {
        @Override
        public String remapClass(Class<?> from) {
            return "";
        }

        @Override
        public String unmapClass(String from) {
            return "";
        }

        @Override
        public String remapField(Class<?> from, Field field) {
            return "";
        }

        @Override
        public String remapMethod(Class<?> from, Method method) {
            return "";
        }
    }
}
