package zzzank.probejs.lang.typescript.code.type;

import zzzank.probejs.lang.typescript.Declaration;
import zzzank.probejs.lang.typescript.code.Code;

import java.util.List;

public abstract class BaseType extends Code {
    public final List<String> format(Declaration declaration) {
        return format(declaration, FormatType.RETURN);
    }

    public abstract List<String> format(Declaration declaration, FormatType input);

    public String line(Declaration declaration, FormatType input) {
        return format(declaration, input).get(0);
    }

    // Stuffs for convenience

    public TSArrayType asArray() {
        return new TSArrayType(this);
    }

    public ContextShield contextShield(FormatType formatType) {
        return new ContextShield(this, formatType);
    }

    public enum FormatType {
        INPUT,
        RETURN,
        VARIABLE
    }
}
