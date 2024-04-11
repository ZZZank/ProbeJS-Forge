package com.probejs.util;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;

public class PUtil {

    private static final String[] INDENT_CACHE;

    static {
        INDENT_CACHE = new String[12 + 1];
        for (int i = 0; i < INDENT_CACHE.length; i++) {
            INDENT_CACHE[i] = String.join("", Collections.nCopies(i, " "));
        }
    }

    public static <T> T tryOrDefault(TrySupplier<T> toEval, T defaultValue) {
        try {
            return toEval.get();
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public interface TrySupplier<T> {
        T get() throws Exception;
    }

    public static String indent(int indentLength) {
        if (indentLength < INDENT_CACHE.length) {
            return INDENT_CACHE[indentLength];
        }
        return String.join("", Collections.nCopies(indentLength, " "));
    }

    @SuppressWarnings("unchecked")
    public static <E> E castedGetOrDef(Object key, Map<?, ?> values, E defaultValue) {
        Object v = values.get(key);
        return v == null ? defaultValue : (E) v;
    }

    @SuppressWarnings("unchecked")
    public static <T> T castedGetField(Field f, Object o, T defaultVal) {
        try {
            return (T) f.get(o);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defaultVal;
    }
}
