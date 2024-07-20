package zzzank.probejs.utils.json;

import com.google.gson.JsonElement;

public interface IJsonBuilder<T extends JsonElement> {
    T build();
}
