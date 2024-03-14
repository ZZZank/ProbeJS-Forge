package com.probejs.formatter.formatter;

import java.util.List;

public interface IFormatter {
    List<String> format(int indent, int stepIndent);
}
