package zzzank.probejs.lang.typescript.code.type;

import zzzank.probejs.lang.java.clazz.ClassPath;
import zzzank.probejs.lang.typescript.Declaration;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class TSOptionalType extends BaseType {
    public BaseType component;

    public TSOptionalType(BaseType component) {
        this.component = component;
    }

    @Override
    public List<String> format(Declaration declaration, FormatType input) {
        return Collections.singletonList(String.format("(%s)?", component.line(declaration, input)));
    }

    @Override
    public Collection<ClassPath> getUsedClassPaths() {
        return component.getUsedClassPaths();
    }
}
