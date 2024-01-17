package com.probejs.util;

import java.util.Collections;

public class PUtil {

    private static final String INDENT_4 = "    ";

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
        if (indentLength == 4) {
            // shortcut sine 4-space indent is very common
            return INDENT_4;
        }
        return String.join("", Collections.nCopies(indentLength, " "));
    }

    public static String getCamelCase(String text) {
        if (text.isEmpty()) {
            return text;
        }
        StringBuilder builder = new StringBuilder(text);
        builder.setCharAt(0, Character.toUpperCase(builder.charAt(0)));
        return builder.toString();
    }
}
