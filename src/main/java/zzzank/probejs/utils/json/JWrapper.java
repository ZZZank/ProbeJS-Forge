package zzzank.probejs.utils.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class JWrapper implements IJsonBuilder<JsonElement> {
    public static final JWrapper NULL = new JWrapper(JsonNull.INSTANCE);

    private final JsonElement raw;

    public static JWrapper of(JsonElement raw) {
        return new JWrapper(raw);
    }

    @Override
    public JsonElement build() {
        return raw;
    }
}
