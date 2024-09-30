package zzzank.probejs.lang.typescript.code.type.js;

import zzzank.probejs.lang.java.clazz.ClassPath;
import zzzank.probejs.lang.typescript.Declaration;
import zzzank.probejs.lang.typescript.code.type.BaseType;
import zzzank.probejs.lang.typescript.refer.ImportInfo;

import java.util.*;
import java.util.stream.Collectors;

public abstract class JSJoinedType extends BaseType {
    public final String delimiter;
    public final List<BaseType> types;

    protected JSJoinedType(String delimiter, List<BaseType> types) {
        this.delimiter = String.format(" %s ", delimiter);
        this.types = types;
    }


    @Override
    public Collection<ImportInfo> getImportInfos() {
        Set<ImportInfo> paths = new HashSet<>();
        for (BaseType type : types) {
            paths.addAll(type.getImportInfos());
        }
        return paths;
    }

    @Override
    public List<String> format(Declaration declaration, FormatType input) {
        return Collections.singletonList(
            types.stream()
                .map(type -> String.format("(%s)", type.line(declaration, input)))
                .collect(Collectors.joining(delimiter))
        );
    }

    public static class Union extends JSJoinedType {
        public Union(List<BaseType> types) {
            super("|", types);
        }
    }

    public static class Intersection extends JSJoinedType {

        public Intersection(List<BaseType> types) {
            super("&", types);
        }
    }
}
