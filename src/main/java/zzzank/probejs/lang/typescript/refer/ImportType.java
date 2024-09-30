package zzzank.probejs.lang.typescript.refer;

import java.util.Objects;
import java.util.function.UnaryOperator;

/**
 * @author ZZZank
 */
public enum ImportType {
    ORIGINAL(UnaryOperator.identity()),
    STATIC(s -> s + "$$Static"),
    TYPE(s -> s + "$$Type")
    ;

    private final UnaryOperator<String> formatter;

    ImportType(UnaryOperator<String> formatter) {
        this.formatter = Objects.requireNonNull(formatter);
    }

    public String fmt(String s) {
        return formatter.apply(s);
    }
}
