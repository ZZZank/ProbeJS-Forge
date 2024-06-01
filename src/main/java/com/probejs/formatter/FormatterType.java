package com.probejs.formatter;

import com.probejs.formatter.resolver.NameResolver;
import com.probejs.info.type.*;
import lombok.val;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class FormatterType<T extends JavaType> {

    public static final Map<Class<? extends JavaType>, Function<JavaType, FormatterType<?>>> REGISTRIES = new HashMap<>();
    public static final Literal LITERAL_ANY = new Literal("any");
    protected boolean underscored = false;

    static {
        REGISTRIES.put(JavaTypeClass.class, Clazz::new);
        REGISTRIES.put(JavaTypeArray.class, Array::new);
        REGISTRIES.put(JavaTypeParameterized.class, Parameterized::new);
        REGISTRIES.put(JavaTypeVariable.class, Variable::new);
        REGISTRIES.put(JavaTypeWildcard.class, Wildcard::new);
    }

    public static FormatterType<? extends JavaType> of(JavaType tInfo) {
        return of(tInfo, true);
    }

    public static FormatterType<? extends JavaType> of(JavaType tInfo, boolean allowSpecial) {
        //special
        if (allowSpecial) {
            val rawClass = tInfo.getResolvedClass();
            val special = NameResolver.specialTypeFormatters.get(rawClass);
            if (special != null) {
                return new Literal(special.apply(tInfo));
            }
        }
        //general
        val builder = REGISTRIES.get(tInfo.getClass());
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
     * {@link JavaType} provided by {@code getInfo()}
     */
    public FormatterType<T> underscored(Predicate<JavaType> predicate) {
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
    public static class Clazz extends FormatterType<JavaTypeClass> {
        private final JavaTypeClass tInfo;

        /**
         * use {@link FormatterType#of(JavaType)} instead
         */
        public Clazz(JavaType tInfo) {
            this.tInfo = (JavaTypeClass) tInfo;
        }

        @Override
        public JavaTypeClass getInfo() {
            return this.tInfo;
        }

        @Override
        public String format() {
            val s = NameResolver.getResolvedName(this.tInfo.getTypeName()).getFullName();
            if (!underscored) {
                return s;
            }
            return s + "_";
        }
    }

    /**
     * '?', '? extends Number', '? super Integer'
     */
    public static class Wildcard extends FormatterType<JavaTypeWildcard> {
        private final JavaTypeWildcard tInfo;

        /**
         * use {@link FormatterType#of(JavaType)} instead
         */
        public Wildcard(JavaType tInfo) {
            this.tInfo = (JavaTypeWildcard) tInfo;
        }

        @Override
        public JavaTypeWildcard getInfo() {
            return this.tInfo;
        }

        @Override
        public String format() {
            return FormatterType.of(this.tInfo.getBase()).underscored(this.underscored).format();
        }
    }

    /**
     * 'T', 'K extends List'
     */
    public static class Variable extends FormatterType<JavaTypeVariable> {
        private final JavaTypeVariable tInfo;

        /**
         * use {@link FormatterType#of(JavaType)} instead
         */
        public Variable(JavaType tInfo) {
            this.tInfo = (JavaTypeVariable) tInfo;
        }

        @Override
        public JavaTypeVariable getInfo() {
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
    public static class Array extends FormatterType<JavaTypeArray> {
        private final JavaTypeArray tInfo;

        /**
         * use {@link FormatterType#of(JavaType)} instead
         */
        public Array(JavaType tInfo) {
            this.tInfo = (JavaTypeArray) tInfo;
        }

        @Override
        public JavaTypeArray getInfo() {
            return this.tInfo;
        }

        @Override
        public String format() {
            return FormatterType.of(this.tInfo.getBase()).underscored(this.underscored).format() + "[]";
        }
    }

    /**
     * {@code Map<String, Boolean>}
     */
    public static class Parameterized extends FormatterType<JavaTypeParameterized> {
        private final JavaTypeParameterized tInfo;

        /**
         * use {@link FormatterType#of(JavaType)} instead
         */
        public Parameterized(JavaType tInfo) {
            this.tInfo = (JavaTypeParameterized) tInfo;
        }

        @Override
        public JavaTypeParameterized getInfo() {
            return this.tInfo;
        }

        @Override
        public String format() {
            return String.format("%s<%s>",
                FormatterType.of(this.tInfo.getBase()).underscored(this.underscored).format(),
                this.tInfo.getParamTypes()
                    .stream()
                    .map(FormatterType::of)
                    .map(FormatterType::format)
                    .collect(Collectors.joining(", "))
            );
        }
    }
}