package zzzank.probejs.lang.typescript.code.type.js;

import lombok.val;
import zzzank.probejs.lang.typescript.Declaration;
import zzzank.probejs.lang.typescript.code.type.BaseType;
import zzzank.probejs.lang.typescript.refer.ImportInfos;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.StringJoiner;

public abstract class JSJoinedType extends BaseType {
    public final String delimiter;
    public final List<BaseType> types;

    protected JSJoinedType(String delimiter, List<BaseType> types) {
        this.delimiter = String.format(" %s ", delimiter);
        this.types = types;
    }

    @Override
    public ImportInfos getImportInfos(@Nonnull FormatType type) {
        return ImportInfos.of().fromCodes(types, type);
    }

    @Override
    public String line(Declaration declaration, FormatType formatType) {
        val joiner = new StringJoiner(delimiter, "(", ")");
        for (val t : types) {
            joiner.add(t.line(declaration, formatType));
        }
        return joiner.toString();
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
