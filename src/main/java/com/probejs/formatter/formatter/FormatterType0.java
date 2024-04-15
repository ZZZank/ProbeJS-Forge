package com.probejs.formatter.formatter;

import com.probejs.formatter.NameResolver;
import com.probejs.info.type.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class FormatterType0<T extends ITypeInfo> {

    public static final Map<Class<? extends ITypeInfo>, Function<ITypeInfo, FormatterType0<?>>> REGISTRIES = new HashMap<>();
    public static final Dummy DUMMY_ANY = new Dummy("any");
    protected boolean underscored = false;

    static {
        REGISTRIES.put(TypeInfoClass.class, Clazz::new);
        REGISTRIES.put(TypeInfoArray.class, Array::new);
        REGISTRIES.put(TypeInfoParameterized.class, Parameterized::new);
        REGISTRIES.put(TypeInfoVariable.class, Variable::new);
        REGISTRIES.put(TypeInfoWildcard.class, Wildcard::new);
    }

    public static FormatterType0<? extends ITypeInfo> of(ITypeInfo tInfo) {
        return of(tInfo, true);
    }

    public static FormatterType0<? extends ITypeInfo> of(ITypeInfo tInfo, boolean allowSpecial) {
        //special
        if (allowSpecial) {
            Class<?> rawClass = tInfo.getResolvedClass();
            Function<ITypeInfo, String> special = NameResolver.specialTypeFormatters.get(rawClass);
            if (special != null) {
                return new Dummy(special.apply(tInfo));
            }
        }
        //general
        Function<ITypeInfo, FormatterType0<?>> builder = REGISTRIES.get(tInfo.getClass());
        if (builder != null) {
            return builder.apply(tInfo);
        }
        //fallback
        return DUMMY_ANY;
    }

    /**
     * same as calling {@code underscored(true)}
     * @return formatter itself
     */
    public FormatterType0<T> underscored() {
        this.underscored(true);
        return this;
    }

    /**
     * set if the formatter should add underscore whenever possible, to refer to all assignable
     * type alias of such type
     * @return formatter itself
     */
    public FormatterType0<T> underscored(boolean under) {
        this.underscored = under;
        return this;
    }

    /**
     * determine the value of "underscored" via provided {@link Predicate}, which will be applied to the
     * {@link ITypeInfo} provided by {@code getInfo()}
     */
    public FormatterType0<T> underscored(Predicate<ITypeInfo> predicate) {
        T info = this.getInfo();
        if (info != null) {
            this.underscored(predicate.test(info));
        }
        return this;
    }

    public abstract T getInfo();

    public abstract String format();

    public static class Dummy extends FormatterType0<ITypeInfo> {

        private final String value;

        private Dummy(String value) {
            this.value = value;
        }

        @Override
        public ITypeInfo getInfo() {
            return null;
        }

        @Override
        public String format() {
            return value;
        }
    }

    public static class Clazz extends FormatterType0<ITypeInfo> {
        private final TypeInfoClass tInfo;

        /**
         * use {@link FormatterType0#of(ITypeInfo)} instead
         */
        @Deprecated
        public Clazz(ITypeInfo tInfo) {
            this.tInfo = (TypeInfoClass) tInfo;
        }

        @Override
        public ITypeInfo getInfo() {
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

    public static class Wildcard extends FormatterType0<ITypeInfo> {
        private final TypeInfoWildcard tInfo;

        /**
         * use {@link FormatterType0#of(ITypeInfo)} instead
         */
        @Deprecated
        public Wildcard(ITypeInfo tInfo) {
            this.tInfo = (TypeInfoWildcard) tInfo;
        }

        @Override
        public ITypeInfo getInfo() {
            return this.tInfo;
        }

        @Override
        public String format() {
            return FormatterType0.of(this.tInfo.getBaseType()).format();
        }
    }

    public static class Variable extends FormatterType0<ITypeInfo> {
        private final TypeInfoVariable tInfo;

        /**
         * use {@link FormatterType0#of(ITypeInfo)} instead
         */
        @Deprecated
        public Variable(ITypeInfo tInfo) {
            this.tInfo = (TypeInfoVariable) tInfo;
        }

        @Override
        public ITypeInfo getInfo() {
            return this.tInfo;
        }

        @Override
        public String format() {
            String bounds = this.tInfo
                .getBounds()
                .stream()
                .map(FormatterType0::of)
                .map(FormatterType0::format)
                .filter(str -> !str.equals("any"))
                .collect(Collectors.joining(", "));
            String name = this.tInfo.getTypeName();
            if (!bounds.isEmpty()) {
                name = String.format("%s extends %s", name, bounds);
            }
            return name;
        }
    }

    public static class Array extends FormatterType0<ITypeInfo> {
        private final TypeInfoArray tInfo;

        /**
         * use {@link FormatterType0#of(ITypeInfo)} instead
         */
        @Deprecated
        public Array(ITypeInfo tInfo) {
            this.tInfo = (TypeInfoArray) tInfo;
        }

        @Override
        public ITypeInfo getInfo() {
            return this.tInfo;
        }

        @Override
        public String format() {
            return FormatterType0.of(this.tInfo.getBaseType()).format() + "[]";
        }
    }

    public static class Parameterized extends FormatterType0<ITypeInfo> {
        private final TypeInfoParameterized tInfo;

        /**
         * use {@link FormatterType0#of(ITypeInfo)} instead
         */
        @Deprecated
        public Parameterized(ITypeInfo tInfo) {
            this.tInfo = (TypeInfoParameterized) tInfo;
        }

        @Override
        public ITypeInfo getInfo() {
            return this.tInfo;
        }

        @Override
        public String format() {
            return String.format("%s<%s>",
                FormatterType0.of(this.tInfo.getBaseType()).format(),
                this.tInfo.getParamTypes()
                    .stream()
                    .map(FormatterType0::of)
                    .map(fmtr -> fmtr.underscored(false))
                    .map(FormatterType0::format)
                    .collect(Collectors.joining(", "))
            );
        }
    }
}
