package zzzank.probejs.features.kubejs;

import com.github.bsideup.jabel.Desugar;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.script.ScriptType;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import zzzank.probejs.utils.CollectUtils;
import zzzank.probejs.utils.JsonUtils;
import zzzank.probejs.utils.Mutable;
import zzzank.probejs.utils.ReflectUtils;

import javax.annotation.Nullable;
import java.util.EnumSet;

/**
 * @author ZZZank
 */
@Desugar
public record EventJSInfo(
    Class<? extends EventJS> clazzRaw,
    String id,
    boolean cancellable,
    EnumSet<ScriptType> scriptTypes,
    Mutable<String> sub
) implements Comparable<EventJSInfo> {

    public EventJSInfo(ScriptType t, EventJS event, String id, @Nullable String sub) {
        this(event.getClass(), id, event.canCancel(), EnumSet.of(t), new Mutable<>(sub));
    }

    @Nullable
    public static EventJSInfo fromJson(String id, JsonObject json) {
        //class
        val clazz = ReflectUtils.classOrNull(json.get("class").getAsString());
        if (clazz == null || !EventJS.class.isAssignableFrom(clazz)) {
            return null;
        }
        //type
        val types = EnumSet.noneOf(ScriptType.class);
        for (val element : json.get("type").getAsJsonArray()) {
            types.add(ScriptType.valueOf(element.getAsString()));
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
            new Mutable<>(sub)
        );
    }

    public boolean hasSub() {
        return sub.get() != null;
    }

    public Pair<String, JsonObject> toJson() {
        val m = CollectUtils.ofMap(
            "class", clazzRaw.getName(),
            "type", CollectUtils.mapToList(scriptTypes, ScriptType::name),
            "cancellable", this.cancellable
        );
        if (sub.notNull()) {
            m.put("sub", sub.get());
        }
        return Pair.of(id, (JsonObject) JsonUtils.parseObject(m));
    }

    @Override
    public int compareTo(@NotNull EventJSInfo o) {
        return this.id.compareTo(o.id);
    }
}
