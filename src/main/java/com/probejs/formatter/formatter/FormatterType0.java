package com.probejs.formatter.formatter;

import com.probejs.formatter.NameResolver;
import com.probejs.info.type.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class FormatterType0<T extends ITypeInfo> {

    public static final Map<Class<? extends ITypeInfo>, Function<ITypeInfo, FormatterType0<?>>> REGISTRIES = new HashMap<>();
    public static final Dummy DUMMY = new Dummy("any");
    protected boolean underscored;

    public static FormatterType0<? extends ITypeInfo> of(ITypeInfo tInfo) {
        Function<ITypeInfo, FormatterType0<?>> builder = REGISTRIES.get(tInfo.getClass());
        if (builder != null) {
            return builder.apply(tInfo);
        }
        return DUMMY;
    }

    public FormatterType0<T> underscored() {
        this.underscored = true;
        return this;
    }

    public FormatterType0<T> underscored(boolean under) {
        this.underscored = under;
        return this;
    }

    protected String withUnderscored(String s) {
        if (!underscored) {
            return s;
        }
        return s + "_";
    }

    public abstract String format();

    public static class Dummy extends FormatterType0<ITypeInfo> {

        private final String value;

        private Dummy(String value) {
            this.value = value;
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
        public String format() {
            String bounds = this.tInfo
                .getBounds()
                .stream()
                .map(FormatterType::new)
                .map(FormatterType::format)
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
