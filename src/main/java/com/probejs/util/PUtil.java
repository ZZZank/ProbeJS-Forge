package com.probejs.util;

import java.util.Collections;

public class PUtil {

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
        if (indentLength == 0) {
            return "";
        }
        if (indentLength == 4) {
            // shortcut sine 4-space indent is very common
            return "    ";
        }
        return String.join("", Collections.nCopies(indentLength, " "));
    }
}
