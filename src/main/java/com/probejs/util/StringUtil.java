package com.probejs.util;

import com.probejs.document.parser.handler.AbstractStackedMachine;
import com.probejs.document.parser.handler.IStateHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.function.Predicate;

public class StringUtil {

    public static final Predicate<Character> PUSH_INDICATOR = c -> c == '(' || c == '{' || c == '<';
    public static final Predicate<Character> POP_INDICATOR = c -> c == ')' || c == '}' || c == '>';

    /**
     * Get the index of {@code target} in {@code str}, with "nested" one ignored. 
     * <p>
     * E.g. If {@code target} is '+', {@code str} is "(1+2)+3", the first
     * '+' will be ignored
     *
     * @param str
     * @param target
     * @return The index, or -1 if not found
     * @see com.probejs.util.StringUtil#PUSH_INDICATOR
     * @see com.probejs.util.StringUtil#POP_INDICATOR
     */
    public static int indexLayered(String str, char target) {
        Stack<Character> stack = new Stack<>();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (stack.empty() && c == target) {
                return i;
            }
            if (PUSH_INDICATOR.test(c)) {
                stack.push(c);
            } else if (POP_INDICATOR.test(c)) {
                if (!stack.empty()) {
                    stack.pop();
                }
            }
        }
        return -1;
    }

    private static class SplitState extends AbstractStackedMachine<String> {

        private int index = 0;

        private SplitState(String push, String pop, String delimiter) {
            stack.add(new Split(push, pop, delimiter));
        }

        @Override
        public void step(String element) {
            super.step(element);
            index++;
        }

        private int getIndex() {
            if (stack.isEmpty()) {
                return index;
            }
            return -1;
        }
    }

    private static class Mask implements IStateHandler<String> {

        private final String push;
        private final String pop;

        private Mask(String push, String pop) {
            this.push = push;
            this.pop = pop;
        }

        @Override
        public void trial(String element, List<IStateHandler<String>> stack) {
            if (element.equals(push)) stack.add(new Mask(push, pop));
            if (element.equals(pop)) stack.remove(this);
        }
    }

    private static class Split implements IStateHandler<String> {

        private final String push;
        private final String pop;
        private final String split;

        private Split(String push, String pop, String split) {
            this.push = push;
            this.pop = pop;
            this.split = split;
        }

        @Override
        public void trial(String element, List<IStateHandler<String>> stack) {
            if (element.equals(push)) {
                stack.add(new Mask(push, pop));
            }
            if (element.equals(split)) {
                stack.remove(this);
            }
        }
    }

    public static int indexLayer(String s, String push, String pop, String delimiter) {
        SplitState state = new SplitState(push, pop, delimiter);
        for (String step : s.split("")) {
            state.step(step);
            if (state.isEmpty()) {
                break;
            }
        }
        return state.getIndex();
    }

    public static Pair<String, String> splitFirst(String s, String push, String pop, String delimiter) {
        int index = StringUtil.indexLayer(s, push, pop, delimiter);
        if (index == -1) {
            return null;
        }
        return new Pair<>(s.substring(0, index - 1), s.substring(index));
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
}
