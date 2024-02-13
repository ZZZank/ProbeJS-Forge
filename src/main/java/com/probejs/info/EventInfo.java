package com.probejs.info;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.script.ScriptType;
import java.util.EnumSet;
import java.util.Optional;
import javax.annotation.Nullable;

public class EventInfo {

    public final Class<? extends EventJS> captured;
    public final String id;
    public final boolean cancellable;
    public final EnumSet<ScriptType> scriptTypes;

    @Nullable
    public final String sub;

    public EventInfo(
        Class<? extends EventJS> captured,
        String id,
        @Nullable String sub,
        EnumSet<ScriptType> scriptTypes,
        boolean cancellable
    ) {
        this.captured = captured;
        this.id = id;
        this.sub = sub;
        this.cancellable = cancellable;
        this.scriptTypes = scriptTypes;
    }

    public EventInfo(ScriptType t, EventJS event, String id, @Nullable String sub) {
        this.captured = event.getClass();
        this.sub = sub;
        this.id = id;
        this.cancellable = event.canCancel();
        this.scriptTypes = EnumSet.of(t);
    }

    public boolean hasSub() {
        return sub != null;
    }

    public boolean isFromCache() {
        return scriptTypes == null;
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("id", id);
        json.addProperty("sub", sub);
        json.addProperty("class", captured.getName());
        JsonArray types = new JsonArray();
        scriptTypes.forEach(script -> types.add(script.name()));
        json.add("type", types);
        json.addProperty("cancellable", cancellable);
        return json;
    }

    @SuppressWarnings("unchecked")
    public static Optional<EventInfo> fromJson(JsonObject json) {
        String id = json.get("id").getAsString();
        Class<?> clazz;
        try {
            clazz = Class.forName(json.get("class").getAsString());
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            return Optional.empty();
        }
        EnumSet<ScriptType> types = EnumSet.noneOf(ScriptType.class);
        if (json.has("type")) {
            JsonArray jArray = json.get("type").getAsJsonArray();
            jArray.forEach(jElement -> types.add(ScriptType.valueOf(jElement.getAsString())));
        }
        String sub = json.has("sub") ? json.get("sub").getAsString() : null;
        boolean cancellable = json.has("cancellable") && json.get("cancellable").getAsBoolean();
        return Optional.of(new EventInfo((Class<? extends EventJS>) clazz, id, sub, types, cancellable));
    }
}
