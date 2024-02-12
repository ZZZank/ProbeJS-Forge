package com.probejs.util;

import java.util.ArrayList;
import java.util.List;

public class StringUtil {

    public static final String PUSH_INDICATOR = "({[<";
    public static final String POP_INDICATOR = ">]})";

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
        return new Pair<>(s.substring(0, index - 1), s.substring(index));
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
            splits.add(splitResult.getFirst());
            s = splitResult.getSecond();
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
}
