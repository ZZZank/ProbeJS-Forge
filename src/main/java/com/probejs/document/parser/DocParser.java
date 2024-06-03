package com.probejs.document.parser;

import java.util.List;
import java.util.Stack;

/**
 * @author ZZZank
 */
public class DocParser {

    private final Stack<Object> stack;
    private final Document doc;

    DocParser(Document target) {
        stack = new Stack<>();
        doc = target;
        //TODO
        stack.add(null);
    }

    public void parse(List<String> lines) {

    }
}
