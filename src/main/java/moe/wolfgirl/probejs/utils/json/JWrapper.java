package moe.wolfgirl.probejs.utils.json;

import com.google.gson.JsonElement;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class JWrapper implements IJsonBuilder<JsonElement> {
    private final JsonElement raw;

    public static JWrapper of(JsonElement raw) {
        return new JWrapper(raw);
    }

    @Override
    public JsonElement build() {
        return raw;
    }
}
