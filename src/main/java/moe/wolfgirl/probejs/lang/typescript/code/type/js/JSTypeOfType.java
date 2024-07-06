package moe.wolfgirl.probejs.lang.typescript.code.type.js;

import moe.wolfgirl.probejs.lang.java.clazz.ClassPath;
import moe.wolfgirl.probejs.lang.java.clazz.Clazz;
import moe.wolfgirl.probejs.lang.typescript.Declaration;
import moe.wolfgirl.probejs.lang.typescript.code.type.BaseType;
import moe.wolfgirl.probejs.lang.typescript.code.type.TSClassType;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class JSTypeOfType extends BaseType {

    public final BaseType inner;
    private final boolean isInterface;

    public JSTypeOfType(BaseType inner) {
        this.inner = inner;
        Clazz clazz = inner instanceof TSClassType classType ? classType.classPath.toClazz() : null;
        this.isInterface = clazz != null && clazz.attribute.isInterface;
    }

    @Override
    public Collection<ClassPath> getUsedClassPaths() {
        return inner.getUsedClassPaths();
    }

    @Override
    public List<String> format(Declaration declaration, FormatType input) {
        return Collections.singletonList(isInterface ?
            inner.line(declaration, FormatType.RETURN) :
            String.format("typeof %s", inner.line(declaration, FormatType.RETURN))
        );
    }
}
