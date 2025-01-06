package zzzank.probejs.lang.typescript.code.member;

import lombok.ToString;
import lombok.val;
import zzzank.probejs.lang.typescript.Declaration;
import zzzank.probejs.lang.typescript.code.type.BaseType;
import zzzank.probejs.utils.NameUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@ToString
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

    public String format(int i, Declaration declaration, BaseType.FormatType formatType) {
        val builder = new StringBuilder();
        if (varArg) {
            builder.append("...");
        }
        builder.append(getArgName(i));
        if (optional) {
            builder.append("?");
        }
        return builder.append(": ").append(type.line(declaration, formatType)).toString();
    }

    private String getArgName(int i) {
        if (!NameUtils.isTSIdentifier(name)) {
            return "arg" + i;
        }
        var out = name;
        while (!NameUtils.isNameSafe(out)) {
            out = out + "_";
        }
        return out;
    }

    public static String formatParams(List<ParamDecl> params, Declaration declaration) {
        return formatParams(params, declaration, BaseType.FormatType.INPUT);
    }

    public static String formatParams(List<ParamDecl> params, Declaration declaration, BaseType.FormatType formatType) {
        val formatted = new ArrayList<String>(params.size());
        for (int i = 0; i < params.size(); i++) {
            var param = params.get(i);
            formatted.add(param.format(i, declaration, formatType));
        }
        return formatted.stream().collect(Collectors.joining(", ", "(", ")"));
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
}
