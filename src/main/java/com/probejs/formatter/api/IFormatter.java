package com.probejs.formatter.api;

import java.util.List;

public interface IFormatter {
    List<String> format(int indent, int stepIndent);
}
