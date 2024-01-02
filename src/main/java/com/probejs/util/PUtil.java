package com.probejs.util;

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
        String s = " ";
        while (s.length() < indentLength) {
            s = s + s;
        }
        if (s.length() == indentLength) {
            return s;
        }
        return s.substring(0, indentLength);
    }
}
