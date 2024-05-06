package com.probejs.formatter.api;

import java.util.List;

public interface IFormatter {
    List<String> formatLines(int indent, int stepIndent);
    String formatStr(int indent, int stepIndent);
}
