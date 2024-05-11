package com.probejs.info;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.probejs.formatter.FormatterComments;
import com.probejs.util.json.JArray;
import com.probejs.util.json.JObject;
import com.probejs.util.json.JPrimitive;
import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.script.ScriptType;
import lombok.val;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

public class EventInfo implements Comparable<EventInfo> {

    private final Class<? extends EventJS> clazzRaw;
    private final String id;
    private final boolean cancellable;
    private final EnumSet<ScriptType> scriptTypes;

    @Nullable
    private final String sub;

    public EventInfo(
        Class<? extends EventJS> captured,
        String id,
        @Nullable String sub,
        EnumSet<ScriptType> scriptTypes,
        boolean cancellable
    ) {
        this.clazzRaw = captured;
        this.id = id;
        this.sub = sub;
        this.cancellable = cancellable;
        this.scriptTypes = scriptTypes;
    }

    public EventInfo(ScriptType t, EventJS event, String id, @Nullable String sub) {
        this.clazzRaw = event.getClass();
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
        return JObject.of()
            .add("id", id)
            .ifThen(hasSub(), (jObj) -> jObj.add("sub", sub))
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

    /**
     * get builtin property as multi-line comments, including `cancellable`, `script
     * types`, and additional info for wildcarded event
     */
    public List<String> getBuiltinPropAsComment() {
        String canCancel = this.cancellable ? "Yes" : "No";
        val typeNames =
            this.scriptTypes.stream().map(type -> type.name).collect(Collectors.toList());
        if (typeNames.isEmpty()) {
            canCancel = "unknown";
            typeNames.add("unknown, info of this event seems fetched from an older version of cache");
        }
        return new FormatterComments("@cancellable " + canCancel, "@at " + String.join(", ", typeNames))
            .setStyle(FormatterComments.CommentStyle.J_DOC)
            .formatLines(0, 0);
    }

    @SuppressWarnings("unchecked")
    public static Optional<EventInfo> fromJson(JsonObject json) {
        //id
        val id = json.get("id").getAsString();
        //class
        Class<?> clazz;
        try {
            clazz = Class.forName(json.get("class").getAsString());
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            return Optional.empty();
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

        return Optional.of(new EventInfo((Class<? extends EventJS>) clazz, id, sub, types, cancellable));
    }

    @Override
    public int compareTo(EventInfo o) {
        return this.id.compareTo(o.id);
    }

    public Class<? extends EventJS> clazzRaw() {
        return clazzRaw;
    }

    public String id() {
        return id;
    }

    public boolean cancellable() {
        return cancellable;
    }

    public EnumSet<ScriptType> scriptTypes() {
        return scriptTypes;
    }

    @Nullable
    public String sub() {
        return sub;
    }
}
