package zzzank.probejs.lang.typescript.code.type;

import lombok.val;
import zzzank.probejs.lang.typescript.Declaration;
import zzzank.probejs.lang.typescript.code.Code;
import zzzank.probejs.lang.typescript.code.type.js.JSJoinedType;
import zzzank.probejs.lang.typescript.code.type.utility.ContextShield;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

    public ContextShield concrete() {
        return contextShield(FormatType.RETURN);
    }

    public ContextShield contextShield(FormatType formatType) {
        return new ContextShield(this, formatType);
    }

    public TSOptionalType optional() {
        return new TSOptionalType(this);
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

    public enum FormatType {
        INPUT,
        RETURN,
        VARIABLE
    }
}
