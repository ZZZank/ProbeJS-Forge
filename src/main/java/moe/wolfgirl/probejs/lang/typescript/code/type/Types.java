package moe.wolfgirl.probejs.lang.typescript.code.type;

import moe.wolfgirl.probejs.ProbeJS;
import moe.wolfgirl.probejs.lang.java.clazz.ClassPath;
import moe.wolfgirl.probejs.lang.typescript.Declaration;
import moe.wolfgirl.probejs.lang.typescript.code.type.js.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public interface Types {
    JSPrimitiveType ANY = new JSPrimitiveType("any");
    JSPrimitiveType BOOLEAN = new JSPrimitiveType("boolean");
    JSPrimitiveType NUMBER = new JSPrimitiveType("number");
    JSPrimitiveType STRING = new JSPrimitiveType("string");
    JSPrimitiveType NEVER = new JSPrimitiveType("never");
    JSPrimitiveType UNKNOWN = new JSPrimitiveType("unknown");
    JSPrimitiveType VOID = new JSPrimitiveType("void");
    JSPrimitiveType THIS = new JSPrimitiveType("this");
    JSPrimitiveType OBJECT = new JSPrimitiveType("object");
    JSPrimitiveType NULL = new JSPrimitiveType("null");
    JSPrimitiveType INSTANCE_TYPE = new JSPrimitiveType("InstanceType");
    JSTupleType EMPTY_ARRAY = Types.tuple().build();

    /**
     * Returns a literal type of the input if it's something OK in TS,
     * otherwise, any will be returned.
     *
     * @param content a string, number or boolean
     */
    static JSPrimitiveType literal(Object content) {
        if (!(content instanceof String || content instanceof Number || content instanceof Boolean))
            return ANY;
        return new JSPrimitiveType(ProbeJS.GSON.toJson(content));
    }

    /**
     * Returns a type that will be as-is in the TypeScript to represent
     * keywords/types not covered, e.g. InstanceType.
     */
    static JSPrimitiveType primitive(String type) {
        return new JSPrimitiveType(type);
    }


    static JSTupleType.Builder tuple() {
        return new JSTupleType.Builder();
    }

    static TSArrayType array(BaseType base) {
        return new TSArrayType(base);
    }

    static JSJoinedType.Intersection and(BaseType... types) {
        return new JSJoinedType.Intersection(Arrays.stream(types).collect(Collectors.toList()));
    }

    static BaseType or(BaseType... types) {
        if (types.length == 0) return NEVER;
        return new JSJoinedType.Union(Arrays.stream(types).collect(Collectors.toList()));
    }

    static TSParamType parameterized(BaseType base, BaseType... params) {
        return new TSParamType(base, Arrays.stream(params).collect(Collectors.toList()));
    }

    static TSVariableType generic(String symbol) {
        return generic(symbol, Types.ANY);
    }

    static TSVariableType generic(String symbol, BaseType extendOn) {
        return new TSVariableType(symbol, extendOn);
    }

    static BaseType typeMaybeGeneric(Class<?> clazz) {
        if (clazz.getTypeParameters().length == 0) return type(clazz);

        var params = Collections.nCopies(clazz.getTypeParameters().length, ANY).toArray(new BaseType[0]);
        return parameterized(type(clazz), params);
    }

    /**
     * You should ensure that this Class does not have type parameters.
     * <br>
     * Otherwise, use typeMaybeGeneric
     */
    static TSClassType type(Class<?> clazz) {
        return type(new ClassPath(clazz));
    }

    static TSClassType type(ClassPath classPath) {
        return new TSClassType(classPath);
    }

    static JSTypeOfType typeOf(Class<?> clazz) {
        return typeOf(new ClassPath(clazz));
    }

    static JSTypeOfType typeOf(ClassPath classPath) {
        return typeOf(new TSClassType(classPath));
    }

    static JSTypeOfType typeOf(BaseType classType) {
        return new JSTypeOfType(classType);
    }

    static TSParamType instanceType(BaseType type) {
        return new TSParamType(INSTANCE_TYPE, Collections.singletonList(type));
    }

    static BaseType ignoreContext(BaseType type, BaseType.FormatType formatType) {
        return new ContextShield(type, formatType);
    }

    static BaseType custom(BiFunction<Declaration, BaseType.FormatType, String> formatter, ClassPath... imports) {
        return new CustomType(formatter, imports);
    }

    static JSLambdaType.Builder lambda() {
        return new JSLambdaType.Builder();
    }

    static JSObjectType.Builder object() {
        return new JSObjectType.Builder();
    }

    static TSOptionalType optional(BaseType type) {
        return new TSOptionalType(type);
    }

    static BaseType filter(BaseType type, Predicate<BaseType> typePredicate) {
        if (type instanceof JSJoinedType.Union union) {
            return new JSJoinedType.Union(
                union.types.stream()
                    .filter(t -> !typePredicate.test(t))
                    .map(t -> filter(t, typePredicate))
                    .collect(Collectors.toList())
            );
        } else if (type instanceof JSJoinedType.Intersection intersection) {
            return new JSJoinedType.Intersection(
                intersection.types.stream()
                    .filter(t -> !typePredicate.test(t))
                    .map(t -> filter(t, typePredicate))
                    .collect(Collectors.toList())
            );
        }
        return type;
    }
}
