package zzzank.probejs.features.kubejs;

import com.github.bsideup.jabel.Desugar;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.script.ScriptType;
import lombok.val;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.NotNull;
import zzzank.probejs.utils.json.JArray;
import zzzank.probejs.utils.json.JObject;
import zzzank.probejs.utils.json.JPrimitive;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Optional;

/**
 * @author ZZZank
 */
@Desugar
public record EventJSInfo(
    Class<? extends EventJS> clazzRaw,
    String id,
    boolean cancellable,
    EnumSet<ScriptType> scriptTypes,
    MutableObject<String> sub
) implements Comparable<EventJSInfo> {

    public EventJSInfo(ScriptType t, EventJS event, String id, @Nullable String sub) {
        this(event.getClass(), id, event.canCancel(), EnumSet.of(t), new MutableObject<>(sub));
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public static EventJSInfo fromJson(JsonObject json) {
        //id
        val id = json.get("id").getAsString();
        //class
        Class<?> clazz;
        try {
            clazz = Class.forName(json.get("class").getAsString());
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            return null;
        }
        //type
        val types = EnumSet.noneOf(ScriptType.class);
        if (json.has("type")) {
            JsonArray jArray = json.get("type").getAsJsonArray();
            jArray.forEach(jElement -> types.add(ScriptType.valueOf(jElement.getAsString())));
        }
        //sub
        val sub = json.has("sub") ? json.get("sub").getAsString() : null;
        //cancellable
        val cancellable = json.has("cancellable") && json.get("cancellable").getAsBoolean();

        return new EventJSInfo(
            (Class<? extends EventJS>) clazz,
            id,
            cancellable,
            types,
            new MutableObject<>(sub)
        );
    }

    public boolean hasSub() {
        return sub.getValue() != null;
    }

    public boolean isFromCache() {
        return scriptTypes == null;
    }

    public JsonObject toJson() {
        return JObject.of()
            .add("id", id)
            .ifThen(hasSub(), (jObj) -> jObj.add("sub", sub.getValue()))
            .add("class", clazzRaw.getName())
            .ifThen(
                scriptTypes != null,
                jObj -> jObj.add(
                    "type",
                    JArray.of().addAll(scriptTypes.stream().map(ScriptType::name).map(JPrimitive::of))
                )
            )
            .add("cancellable", this.cancellable)
            .build();
    }

    @Override
    public int compareTo(@NotNull EventJSInfo o) {
        return this.id.compareTo(o.id);
    }
}
