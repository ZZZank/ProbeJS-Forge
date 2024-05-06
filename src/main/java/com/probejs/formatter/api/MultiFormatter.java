package com.probejs.formatter.api;

public interface MultiFormatter extends IFormatter {
    @Override
    default String formatStr(int indent, int stepIndent) {
        return String.join("\n",formatLines(indent, stepIndent));
    }
}
