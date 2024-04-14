package com.probejs.util;

import com.probejs.ProbeJS;
import dev.latvian.mods.rhino.ContextFactory;
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
    private static RemapperBridge.DummyIRemapper reference = null;

    public static RemapperBridge.DummyIRemapper getRemapper() {
        return RemapperBridge.reference;
    }

    public static void refreshRemapper() {
        if (!Platform.getMod("rhino").getName().equals("Rhizo")) {
            ProbeJS.LOGGER.error("The game seems to be using Rhino instead of newer Rhizo, skipping Remapper check");
            return;
        }
        ProbeJS.LOGGER.error("Refreshing Remapper reference");
        try {
            Field m = ContextFactory.class.getField("remapper");
            m.setAccessible(true);
            RemapperBridge.reference = (RemapperBridge.DummyIRemapper) m.get(null);
            ProbeJS.LOGGER.error("Remapper reference refreshed");
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
}
