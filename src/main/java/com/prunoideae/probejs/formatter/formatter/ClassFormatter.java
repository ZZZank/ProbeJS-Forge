package com.prunoideae.probejs.formatter.formatter;

import com.prunoideae.probejs.document.ClassDocument;
import com.prunoideae.probejs.document.IDocumented;
import com.prunoideae.probejs.info.ClassInfo;

import java.util.List;

public class ClassFormatter implements IDocumented<ClassDocument>, IFormatter {
    private final ClassInfo classInfo;

    public ClassFormatter(ClassInfo classInfo) {
        this.classInfo = classInfo;
    }

    @Override
    public void setDocument(ClassDocument document) {

    }

    @Override
    public List<String> format(Integer indent, Integer stepIndent) {
        return null;
    }
}
