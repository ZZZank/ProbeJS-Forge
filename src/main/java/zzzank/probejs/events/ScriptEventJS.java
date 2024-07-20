package zzzank.probejs.events;

import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.script.ScriptType;
import zzzank.probejs.lang.transpiler.TypeConverter;
import zzzank.probejs.lang.typescript.ScriptDump;

public class ScriptEventJS extends EventJS {
    protected final ScriptDump dump;

    public ScriptEventJS(ScriptDump dump) {
        this.dump = dump;
    }

    public ScriptType getScriptType() {
        return dump.scriptType;
    }

    public TypeConverter getTypeConverter() {
        return dump.transpiler.typeConverter;
    }
}
