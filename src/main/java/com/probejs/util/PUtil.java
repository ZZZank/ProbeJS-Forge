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

    /**
     * Gets a String with its first char set to lower case, like `AABB`->`aABB`
     * @param text The original string
     * @return The original string if it's already lower case in first char, or a
     * new, processed string
     */
    public static String withLowerCaseHead(String text) {
        if (text.isEmpty() || Character.isLowerCase(text.charAt(0))) {
            return text;
        }
        char[] arr = text.toCharArray();
        arr[0] = Character.toLowerCase(arr[0]);
        return new String(arr);
    }
}
