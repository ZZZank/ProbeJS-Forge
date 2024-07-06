package moe.wolfgirl.probejs.lang.typescript.code.type;

import moe.wolfgirl.probejs.lang.java.clazz.ClassPath;
import moe.wolfgirl.probejs.lang.typescript.Declaration;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class TSVariableType extends BaseType {
    public final String symbol;
    public BaseType extendsType;

    public TSVariableType(String symbol, @Nullable BaseType extendsType) {
        this.symbol = symbol;
        this.extendsType = extendsType == Types.ANY ? null : extendsType;
    }

    @Override
    public Collection<ClassPath> getUsedClassPaths() {
        return extendsType == null ? Collections.emptyList() : extendsType.getUsedClassPaths();
    }

    @Override
    public List<String> format(Declaration declaration, FormatType input) {
        return Arrays.asList(switch (input) {
            case INPUT, RETURN -> symbol;
            case VARIABLE -> extendsType == null ? symbol :
                String.format("%s extends %s", symbol, extendsType.line(declaration, FormatType.RETURN));
        });
    }
}
