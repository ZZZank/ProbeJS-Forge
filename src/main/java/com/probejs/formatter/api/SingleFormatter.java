package com.probejs.formatter.api;

import java.util.Collections;
import java.util.List;

public interface SingleFormatter extends IFormatter {

    @Override
    default List<String> formatLines(int indent, int stepIndent) {
        return Collections.singletonList(formatStr(indent, stepIndent));
    }
}
