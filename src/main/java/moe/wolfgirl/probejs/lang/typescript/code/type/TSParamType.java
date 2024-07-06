package moe.wolfgirl.probejs.lang.typescript.code.type;

import moe.wolfgirl.probejs.lang.java.clazz.ClassPath;
import moe.wolfgirl.probejs.lang.typescript.Declaration;

import java.util.*;
import java.util.stream.Collectors;

public class TSParamType extends BaseType {
    public BaseType baseType;
    public List<BaseType> params;

    public TSParamType(BaseType baseType, List<BaseType> params) {
        this.baseType = baseType;
        this.params = new ArrayList<>(params);
    }

    @Override
    public Collection<ClassPath> getUsedClassPaths() {
        Set<ClassPath> paths = new HashSet<>(baseType.getUsedClassPaths());
        for (BaseType param : params) {
            paths.addAll(param.getUsedClassPaths());
        }
        return paths;
    }

    @Override
    public List<String> format(Declaration declaration, FormatType input) {
        return Collections.singletonList(
            String.format(
                "%s<%s>",
                baseType.line(declaration, input),
                params.stream()
                    .map(type -> String.format("(%s)", type.line(declaration, input)))
                    .collect(Collectors.joining(", "))
            )
        );
    }
}
