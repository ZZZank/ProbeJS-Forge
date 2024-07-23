package zzzank.probejs.lang.typescript.code.type.js;

import com.github.bsideup.jabel.Desugar;
import zzzank.probejs.lang.typescript.Declaration;
import zzzank.probejs.lang.typescript.code.type.BaseType;

import java.util.function.Function;

@Desugar
public record JSParam(String name, boolean optional, BaseType type) {
    public String format(Declaration declaration, BaseType.FormatType formatType, Function<String, String> nameGetter) {
        return String.format(
            "%s%s: %s",
            nameGetter.apply(name),
            optional ? "?" : "",
            type.line(declaration, formatType)
        );
    }
}
