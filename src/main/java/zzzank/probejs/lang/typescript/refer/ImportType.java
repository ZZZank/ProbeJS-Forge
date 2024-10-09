package zzzank.probejs.lang.typescript.refer;

import com.google.common.collect.ImmutableList;

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

    public static final ImmutableList<ImportType> ALL = ImmutableList.copyOf(ImportType.values());

    private final UnaryOperator<String> formatter;

    ImportType(UnaryOperator<String> formatter) {
        this.formatter = Objects.requireNonNull(formatter);
    }

    public String fmt(String s) {
        return formatter.apply(s);
    }
}
