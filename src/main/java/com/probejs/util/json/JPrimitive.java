package com.probejs.util.json;

import com.google.gson.JsonPrimitive;

public class JPrimitive implements IJsonBuilder<JsonPrimitive> {

    public static JPrimitive of(Character value) {
        return new JPrimitive(value);
    }

    public static JPrimitive of(String value) {
        return new JPrimitive(value);
    }

    public static JPrimitive of(Number value) {
        return new JPrimitive(value);
    }

    public static JPrimitive of(Boolean value) {
        return new JPrimitive(value);
    }

    private final JsonPrimitive value;

    public JPrimitive(Character value) {
        this.value = new JsonPrimitive(value);
    }

    public JPrimitive(String value) {
        this.value = new JsonPrimitive(value);
    }

    public JPrimitive(Number value) {
        this.value = new JsonPrimitive(value);
    }

    public JPrimitive(boolean value) {
        this.value = new JsonPrimitive(value);
    }


    @Override
    public JsonPrimitive build() {
        return value;
    }

    @Override
    public String toString() {
        return build().toString();
    }
}
