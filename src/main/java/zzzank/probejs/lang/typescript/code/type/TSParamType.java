package zzzank.probejs.lang.typescript.code.type;

import zzzank.probejs.lang.java.clazz.ClassPath;
import zzzank.probejs.lang.typescript.Declaration;
import zzzank.probejs.lang.typescript.refer.ImportInfo;

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
    public Collection<ImportInfo> getImportInfos() {
        Set<ImportInfo> paths = new HashSet<>(baseType.getImportInfos());
        for (BaseType param : params) {
            paths.addAll(param.getImportInfos());
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
