package zzzank.probejs.lang.typescript.code.type;

import lombok.val;
import zzzank.probejs.lang.typescript.Declaration;
import zzzank.probejs.lang.typescript.code.Code;
import zzzank.probejs.lang.typescript.code.type.js.JSJoinedType;
import zzzank.probejs.lang.typescript.code.type.ts.TSArrayType;
import zzzank.probejs.lang.typescript.code.type.ts.TSOptionalType;
import zzzank.probejs.lang.typescript.code.type.ts.TSParamType;
import zzzank.probejs.lang.typescript.code.type.utility.ContextShield;
import zzzank.probejs.lang.typescript.code.type.utility.ImportShield;
import zzzank.probejs.lang.typescript.code.type.utility.WithFormatType;
import zzzank.probejs.lang.typescript.refer.ImportInfos;

import javax.annotation.Nonnull;
import java.util.*;

public abstract class BaseType extends Code {
    @Override
    public final ImportInfos getImportInfos() {
        return getImportInfos(FormatType.RETURN);
    }

    public abstract ImportInfos getImportInfos(@Nonnull FormatType type);

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

    public ContextShield<BaseType> contextShield(FormatType formatType) {
        return new ContextShield<>(this, formatType);
    }

    public ImportShield<BaseType> importShield(ImportInfos imports) {
        return new ImportShield<>(this, imports);
    }

    public TSOptionalType optional() {
        return new TSOptionalType(this);
    }

    public TSParamType withParams(BaseType... params) {
        return Types.parameterized(this, params);
    }

    public TSParamType withParams(Collection<? extends BaseType> params) {
        return Types.parameterized(this, params);
    }

    public JSJoinedType.Union or(BaseType... types) {
        val selfTypes = this instanceof JSJoinedType.Union u
            ? u.types
            : Collections.singletonList(this);
        val joined = new ArrayList<BaseType>(selfTypes.size() + types.length);
        joined.addAll(selfTypes);
        joined.addAll(Arrays.asList(types));
        return new JSJoinedType.Union(joined);
    }

    public JSJoinedType.Intersection and(BaseType... types) {
        val selfTypes = this instanceof JSJoinedType.Intersection i
            ? i.types
            : Collections.singletonList(this);
        val joined = new ArrayList<BaseType>(selfTypes.size() + types.length);
        joined.addAll(selfTypes);
        joined.addAll(Arrays.asList(types));
        return new JSJoinedType.Intersection(joined);
    }

    public WithFormatType comment(String comment) {
        return Types.withComment(this, comment);
    }

    public enum FormatType {
        INPUT,
        RETURN,
        VARIABLE
    }
}
