package com.probejs.document.type;

import lombok.AllArgsConstructor;

import java.util.function.BiFunction;

/**
 * @author ZZZank
 */
@AllArgsConstructor
public abstract class TypePrimitive<T> implements DocType {

    protected T value = null;

    @Override
    public String transform(BiFunction<DocType, String, String> transformer) {
        if (value != null) {
            return value.toString();
        }
        return getTypeName();
    }

    public static class Str extends TypePrimitive<String> {

        public Str(String value) {
            super(value);
        }

        public Str() {
            super(null);
        }
        @Override
        public String getTypeName() {
            return "string";
        }
    }
}
