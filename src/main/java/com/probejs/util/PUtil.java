package com.probejs.util;

import java.util.Collections;

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
        if (indentLength < 12) {
            return INDENT_CACHE[indentLength];
        }
        return String.join("", Collections.nCopies(indentLength, " "));
    }
}
