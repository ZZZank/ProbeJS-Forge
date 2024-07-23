package zzzank.probejs.lang.typescript.code.member;

import zzzank.probejs.lang.typescript.Declaration;
import zzzank.probejs.lang.typescript.code.type.BaseType;
import zzzank.probejs.utils.NameUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class ParamDecl {
    public String name;
    public BaseType type;
    public boolean varArg;
    public boolean optional;

    public ParamDecl(String name, BaseType type, boolean varArg, boolean optional) {
        this.name = name;
        this.type = type;
        this.varArg = varArg;
        this.optional = optional;
    }

    public ParamDecl(String name, BaseType type) {
        this(name, type, false, false);
    }

    public String format(int index, Declaration declaration) {
        return String.format(
            "%s%s%s: %s",
            varArg ? "..." : "",
            NameUtils.isNameSafe(name) ? name : String.format("arg%d", index),
            optional ? "?" : "",
            type.line(declaration, BaseType.FormatType.INPUT)
        );
    }

    public static String formatParams(List<ParamDecl> params, Declaration declaration) {
        List<String> formattedParams = new ArrayList<>();
        for (int i = 0; i < params.size(); i++) {
            ParamDecl param = params.get(i);
            formattedParams.add(param.format(i, declaration));
        }
        return String.format("(%s)", String.join(", ", formattedParams));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ParamDecl) obj;
        return Objects.equals(this.name, that.name) &&
                Objects.equals(this.type, that.type) &&
                this.varArg == that.varArg &&
                this.optional == that.optional;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type, varArg, optional);
    }

    @Override
    public String toString() {
        return "ParamDecl[" +
                "name=" + name + ", " +
                "type=" + type + ", " +
                "varArg=" + varArg + ", " +
                "optional=" + optional + ']';
    }

}
