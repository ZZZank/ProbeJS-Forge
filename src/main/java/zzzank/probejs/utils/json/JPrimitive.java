package zzzank.probejs.utils.json;

import com.google.gson.JsonPrimitive;
import lombok.AllArgsConstructor;

import javax.annotation.Nonnull;

@AllArgsConstructor
public class JPrimitive implements IJsonBuilder<JsonPrimitive> {

    public static JPrimitive of(@Nonnull Character value) {
        return new JPrimitive(new JsonPrimitive(value));
    }

    public static JPrimitive of(@Nonnull String value) {
        return new JPrimitive(new JsonPrimitive(value));
    }

    public static JPrimitive of(@Nonnull Number value) {
        return new JPrimitive(new JsonPrimitive(value));
    }

    public static JPrimitive of(@Nonnull Boolean value) {
        return new JPrimitive(new JsonPrimitive(value));
    }

    private final JsonPrimitive value;

    @Override
    public JsonPrimitive build() {
        return value;
    }

    @Override
    public String toString() {
        return build().toString();
    }
}
