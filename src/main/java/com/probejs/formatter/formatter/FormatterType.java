package com.probejs.formatter.formatter;

import com.probejs.formatter.NameResolver;
import com.probejs.info.type.*;
import com.probejs.info.type.ts.TypeIntersection;
import com.probejs.info.type.ts.TypeLiteral;
import com.probejs.info.type.ts.TypeUnion;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class FormatterType<T extends ITypeInfo> {

    public static final Map<Class<? extends ITypeInfo>, Function<ITypeInfo, FormatterType<?>>> REGISTRIES = new HashMap<>();
    public static final Literal LITERAL_ANY = new Literal("any");
    protected boolean underscored = false;

    static {
        REGISTRIES.put(TypeInfoClass.class, Clazz::new);
        REGISTRIES.put(TypeInfoArray.class, Array::new);
        REGISTRIES.put(TypeInfoParameterized.class, Parameterized::new);
        REGISTRIES.put(TypeInfoVariable.class, Variable::new);
        REGISTRIES.put(TypeInfoWildcard.class, Wildcard::new);
        REGISTRIES.put(TypeUnion.class, Union::new);
        REGISTRIES.put(TypeIntersection.class, Intersection::new);
    }

    public static FormatterType<? extends ITypeInfo> of(ITypeInfo tInfo) {
        return of(tInfo, true);
    }

    public static FormatterType<? extends ITypeInfo> of(ITypeInfo tInfo, boolean allowSpecial) {
        //special
        if (allowSpecial) {
            Class<?> rawClass = tInfo.getResolvedClass();
            Function<ITypeInfo, String> special = NameResolver.specialTypeFormatters.get(rawClass);
            if (special != null) {
                return new Literal(special.apply(tInfo));
            }
        }
        //general
        Function<ITypeInfo, FormatterType<?>> builder = REGISTRIES.get(tInfo.getClass());
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
     * {@link ITypeInfo} provided by {@code getInfo()}
     */
    public FormatterType<T> underscored(Predicate<ITypeInfo> predicate) {
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
    public static class Clazz extends FormatterType<TypeInfoClass> {
        private final TypeInfoClass tInfo;

        /**
         * use {@link FormatterType#of(ITypeInfo)} instead
         */
        public Clazz(ITypeInfo tInfo) {
            this.tInfo = (TypeInfoClass) tInfo;
        }

        @Override
        public TypeInfoClass getInfo() {
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
    public static class Wildcard extends FormatterType<TypeInfoWildcard> {
        private final TypeInfoWildcard tInfo;

        /**
         * use {@link FormatterType#of(ITypeInfo)} instead
         */
        public Wildcard(ITypeInfo tInfo) {
            this.tInfo = (TypeInfoWildcard) tInfo;
        }

        @Override
        public TypeInfoWildcard getInfo() {
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
    public static class Variable extends FormatterType<TypeInfoVariable> {
        private final TypeInfoVariable tInfo;

        /**
         * use {@link FormatterType#of(ITypeInfo)} instead
         */
        public Variable(ITypeInfo tInfo) {
            this.tInfo = (TypeInfoVariable) tInfo;
        }

        @Override
        public TypeInfoVariable getInfo() {
            return this.tInfo;
        }

        @Override
        public String format() {
            String name = this.tInfo.getTypeName();
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
    public static class Array extends FormatterType<TypeInfoArray> {
        private final TypeInfoArray tInfo;

        /**
         * use {@link FormatterType#of(ITypeInfo)} instead
         */
        public Array(ITypeInfo tInfo) {
            this.tInfo = (TypeInfoArray) tInfo;
        }

        @Override
        public TypeInfoArray getInfo() {
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
    public static class Parameterized extends FormatterType<TypeInfoParameterized> {
        private final TypeInfoParameterized tInfo;

        /**
         * use {@link FormatterType#of(ITypeInfo)} instead
         */
        public Parameterized(ITypeInfo tInfo) {
            this.tInfo = (TypeInfoParameterized) tInfo;
        }

        @Override
        public TypeInfoParameterized getInfo() {
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

    /**
     * 'string | number'
     */
    public static class Union extends FormatterType<TypeUnion> {
        private final TypeUnion tInfo;

        /**
         * use {@link FormatterType#of(ITypeInfo)} instead
         */
        public Union(ITypeInfo tInfo) {
            this.tInfo = (TypeUnion) tInfo;
        }

        @Override
        public TypeUnion getInfo() {
            return this.tInfo;
        }

        @Override
        public String format() {
            return FormatterType.of(this.tInfo.left()).underscored(this.underscored).format()
                + " | "
                + FormatterType.of(this.tInfo.right()).underscored(this.underscored).format();
        }
    }
    /**
     * 'string | number'
     */
    public static class Intersection extends FormatterType<TypeIntersection> {
        private final TypeIntersection tInfo;

        /**
         * use {@link FormatterType#of(ITypeInfo)} instead
         */
        public Intersection(ITypeInfo tInfo) {
            this.tInfo = (TypeIntersection) tInfo;
        }

        @Override
        public TypeIntersection getInfo() {
            return this.tInfo;
        }

        @Override
        public String format() {
            return FormatterType.of(this.tInfo.left()).underscored(this.underscored).format()
                + " & "
                + FormatterType.of(this.tInfo.right()).underscored(this.underscored).format();
        }
    }
}
