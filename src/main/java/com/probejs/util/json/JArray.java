package com.probejs.util.json;

import com.google.gson.JsonArray;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class JArray implements IJsonBuilder<JsonArray> {

    public static JArray of() {
        return new JArray();
    }

    public static JArray of(Iterable<IJsonBuilder<?>> members) {
        return JArray.of().addAll(members);
    }

    private JArray() {
        members = new ArrayList<>(3);
    }

    private final List<IJsonBuilder<?>> members;

    public JArray ifThen(boolean condition, Consumer<JArray> action) {
        if (condition) {
            action.accept(this);
        }
        return this;
    }

    public JArray add(IJsonBuilder<?> member) {
        members.add(member);
        return this;
    }

    public JArray add(Character c) {
        return this.add(JPrimitive.of(c));
    }

    public JArray add(String str) {
        return this.add(JPrimitive.of(str));
    }

    public JArray add(Number num) {
        return this.add(JPrimitive.of(num));
    }

    public JArray add(Boolean bool) {
        return this.add(JPrimitive.of(bool));
    }

    public JArray addAll(Iterable<IJsonBuilder<?>> members) {
        for (IJsonBuilder<?> member : members) {
            if (member != null) {
                this.members.add(member);
            }
        }
        return this;
    }

    public JArray addAll(Stream<IJsonBuilder<?>> members) {
        members.filter(Objects::nonNull).forEach(this.members::add);
        return this;
    }

    @Override
    public JsonArray build() {
        JsonArray array = new JsonArray();
        for (IJsonBuilder<?> member : members) {
            array.add(member.build());
        }
        return array;
    }

    @Override
    public String toString() {
        return build().toString();
    }
}
