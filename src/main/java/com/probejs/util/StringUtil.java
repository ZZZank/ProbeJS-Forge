package com.probejs.util;

import java.util.ArrayList;
import java.util.List;

public abstract class StringUtil {

    public static final String PUSH_INDICATOR = "({[<";
    public static final String POP_INDICATOR = ">]})";

    /**
     * "snake_to_camel" -> "SnakeToCamel"
     */
    public static String snakeToCamel(String s) {
        String[] split = s.split("_");
        for (int i = 0; i < split.length; i++) {
            split[i] = withUpperCaseHead(split[i]);
        }
        return String.join("", split);
    }

    /**
     * Get the index of {@code delimiter} in {@code str}, with "nested" one ignored.
     * <p>
     * E.g. If {@code delimiter} is '+', {@code str} is "(1+2)+3", the first
     * '+' will be ignored
     *
     * @param delimiter its length should be 1
     * @return the index of `delimiter`, or -1 when not found
     * @see com.probejs.util.StringUtil#PUSH_INDICATOR
     * @see com.probejs.util.StringUtil#POP_INDICATOR
     */
    public static int indexLayer(String str, String delimiter) {
        return indexLayer(str, PUSH_INDICATOR, POP_INDICATOR, delimiter);
    }

    /**
     *
     * @param delimiter its length should be 1
     * @return the index of `delimiter`, or -1 when not found
     */
    public static int indexLayer(String str, String push, String pop, String delimiter) {
        int depth = 0;
        int index = 0;
        for (String c : str.split("")) {
            if (push.contains(c)) {
                depth++;
            } else if (pop.contains(c) && depth != 0) {
                depth--;
            } else if (depth == 0 && delimiter.equals(c)) {
                return index;
            }
            index++;
        }
        return -1;
    }

    public static Pair<String, String> splitFirst(String s, String push, String pop, String delimiter) {
        int index = StringUtil.indexLayer(s, push, pop, delimiter);
        if (index == -1) {
            return null;
        }
        return new Pair<>(s.substring(0, index), s.substring(index + 1));
    }

    /**
     * @see com.probejs.util.StringUtil#PUSH_INDICATOR
     * @see com.probejs.util.StringUtil#POP_INDICATOR
     */
    public static Pair<String, String> splitFirst(String s, String delimiter) {
        return splitFirst(s, PUSH_INDICATOR, POP_INDICATOR, delimiter);
    }

    public static List<String> splitLayer(String s, String push, String pop, String delimiter) {
        List<String> splits = new ArrayList<>();
        Pair<String, String> splitResult = splitFirst(s, push, pop, delimiter);
        while (splitResult != null) {
            splits.add(splitResult.first);
            s = splitResult.second;
            splitResult = splitFirst(s, push, pop, delimiter);
        }
        splits.add(s);
        return splits;
    }

    /**
     * @see com.probejs.util.StringUtil#PUSH_INDICATOR
     * @see com.probejs.util.StringUtil#POP_INDICATOR
     */
    public static List<String> splitLayer(String s, String delimiter) {
        return splitLayer(s, PUSH_INDICATOR, POP_INDICATOR, delimiter);
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

    /**
     * Gets a String with its first char set to lower case, like `AABB`->`aABB`
     * @param text The original string
     * @return The original string if it's already lower case in first char, or a
     * new, processed string
     */
    public static String withUpperCaseHead(String text) {
        if (text.isEmpty() || Character.isUpperCase(text.charAt(0))) {
            return text;
        }
        char[] arr = text.toCharArray();
        arr[0] = Character.toUpperCase(arr[0]);
        return new String(arr);
    }
}
