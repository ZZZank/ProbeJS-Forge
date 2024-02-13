package com.probejs.info;

import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.script.ScriptType;
import javax.annotation.Nullable;

public class EventInfo {

    public final Class<? extends EventJS> captured;
    public final String id;
    public final boolean canCancel;
    public final ScriptType scriptType;

    @Nullable
    public final String sub;

    /**
     * In this case, `scriptType` will be `null`
     */
    public EventInfo(Class<? extends EventJS> captured, String id, @Nullable String sub) {
        this.captured = captured;
        this.id = id;
        this.sub = sub;
        this.canCancel = false;
        this.scriptType = null;
    }

    public EventInfo(ScriptType t, EventJS event, String id, @Nullable String sub) {
        this.captured = event.getClass();
        this.sub = sub;
        this.id = id;
        this.canCancel = event.canCancel();
        this.scriptType = t;
    }

    public boolean hasSub() {
        return sub != null;
    }

    public boolean isFromCache() {
        return scriptType == null;
    }
}
