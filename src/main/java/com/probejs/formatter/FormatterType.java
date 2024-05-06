package com.probejs.formatter;

import com.probejs.formatter.resolver.NameResolver;
import com.probejs.info.type.*;
import com.probejs.info.type.TypeLiteral;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class FormatterType<T extends IType> {

    public static final Map<Class<? extends IType>, Function<IType, FormatterType<?>>> REGISTRIES = new HashMap<>();
    public static final Literal LITERAL_ANY = new Literal("any");
    protected boolean underscored = false;

    static {
        REGISTRIES.put(TypeClass.class, Clazz::new);
        REGISTRIES.put(TypeArray.class, Array::new);
        REGISTRIES.put(TypeParameterized.class, Parameterized::new);
        REGISTRIES.put(TypeVariable.class, Variable::new);
        REGISTRIES.put(TypeWildcard.class, Wildcard::new);
    }

    public static FormatterType<? extends IType> of(IType tInfo) {
        return of(tInfo, true);
    }

    public static FormatterType<? extends IType> of(IType tInfo, boolean allowSpecial) {
        //special
        if (allowSpecial) {
            Class<?> rawClass = tInfo.getResolvedClass();
            Function<IType, String> special = NameResolver.specialTypeFormatters.get(rawClass);
            if (special != null) {
                return new Literal(special.apply(tInfo));
            }
        }
        //general
        Function<IType, FormatterType<?>> builder = REGISTRIES.get(tInfo.getClass());
        if (builder != null) {
            return builder.apply(tInfo);
        }
        //fallback
        return LITERAL_ANY;
    }

    /**
     * same as calling {@code underscored(true)}
     * @return formatter itself
     */
    public FormatterType<T> underscored() {
        this.underscored(true);
        return this;
    }

    /**
     * set if the formatter should add underscore whenever possible, to refer to all assignable
     * type alias of such type
     * @return formatter itself
     */
    public FormatterType<T> underscored(boolean under) {
        this.underscored = under;
        return this;
    }

    /**
     * determine the value of "underscored" via provided {@link Predicate}, which will be applied to the
     * {@link IType} provided by {@code getInfo()}
     */
    public FormatterType<T> underscored(Predicate<IType> predicate) {
        T info = this.getInfo();
        if (info != null) {
            this.underscored(predicate.test(info));
        }
        return this;
    }

    public abstract T getInfo();

    public abstract String format();

    public static class Literal extends FormatterType<TypeLiteral> {

        private final String value;

        public Literal(String value) {
            this.value = value;
        }

        @Override
        public TypeLiteral getInfo() {
            return new TypeLiteral(this.value);
        }

        @Override
        public String format() {
            return value;
        }
    }

    /**
     * 'String', 'List'
     */
    public static class Clazz extends FormatterType<TypeClass> {
        private final TypeClass tInfo;

        /**
         * use {@link FormatterType#of(IType)} instead
         */
        public Clazz(IType tInfo) {
            this.tInfo = (TypeClass) tInfo;
        }

        @Override
        public TypeClass getInfo() {
            return this.tInfo;
        }

        @Override
        public String format() {
            String s = NameResolver.getResolvedName(this.tInfo.getTypeName()).getFullName();
            if (!underscored) {
                return s;
            }
            return s + "_";
        }
    }

    /**
     * '?', '? extends Number', '? super Integer'
     */
    public static class Wildcard extends FormatterType<TypeWildcard> {
        private final TypeWildcard tInfo;

        /**
         * use {@link FormatterType#of(IType)} instead
         */
        public Wildcard(IType tInfo) {
            this.tInfo = (TypeWildcard) tInfo;
        }

        @Override
        public TypeWildcard getInfo() {
            return this.tInfo;
        }

        @Override
        public String format() {
            return FormatterType.of(this.tInfo.getBaseType()).underscored(this.underscored).format();
        }
    }

    /**
     * 'T', 'K extends List'
     */
    public static class Variable extends FormatterType<TypeVariable> {
        private final TypeVariable tInfo;

        /**
         * use {@link FormatterType#of(IType)} instead
         */
        public Variable(IType tInfo) {
            this.tInfo = (TypeVariable) tInfo;
        }

        @Override
        public TypeVariable getInfo() {
            return this.tInfo;
        }

        @Override
        public String format() {
            String name = this.tInfo.getTypeName();
            if (this.underscored) {
                name = name + "_";
            }
            /*
            String bounds = this.tInfo
                .getBounds()
                .stream()
                .map(FormatterType::of)
                .map(FormatterType::format)
                .filter(str -> !str.equals("any"))
                .collect(Collectors.joining(", "));
            if (!bounds.isEmpty()) {
                name = String.format("%s extends %s", name, bounds);
            }
            */
            return name;
        }
    }

    /**
     * 'int[]'
     */
    public static class Array extends FormatterType<TypeArray> {
        private final TypeArray tInfo;

        /**
         * use {@link FormatterType#of(IType)} instead
         */
        public Array(IType tInfo) {
            this.tInfo = (TypeArray) tInfo;
        }

        @Override
        public TypeArray getInfo() {
            return this.tInfo;
        }

        @Override
        public String format() {
            return FormatterType.of(this.tInfo.getBaseType()).underscored(this.underscored).format() + "[]";
        }
    }

    /**
     * {@code Map<String, Boolean>}
     */
    public static class Parameterized extends FormatterType<TypeParameterized> {
        private final TypeParameterized tInfo;

        /**
         * use {@link FormatterType#of(IType)} instead
         */
        public Parameterized(IType tInfo) {
            this.tInfo = (TypeParameterized) tInfo;
        }

        @Override
        public TypeParameterized getInfo() {
            return this.tInfo;
        }

        @Override
        public String format() {
            return String.format("%s<%s>",
                FormatterType.of(this.tInfo.getBaseType()).underscored(this.underscored).format(),
                this.tInfo.getParamTypes()
                    .stream()
                    .map(FormatterType::of)
                    .map(FormatterType::format)
                    .collect(Collectors.joining(", "))
            );
        }
    }
}