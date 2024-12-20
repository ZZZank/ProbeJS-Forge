package zzzank.probejs.events;

import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.script.ScriptType;
import zzzank.probejs.lang.typescript.ScriptDump;
import zzzank.probejs.lang.typescript.code.ts.Statements;
import zzzank.probejs.lang.typescript.code.type.Types;

public class ScriptEventJS extends EventJS {
    public final ScriptDump scriptDump;

    public ScriptEventJS(ScriptDump scriptDump) {
        this.scriptDump = scriptDump;
    }

    public ScriptType getScriptType() {
        return scriptDump.scriptType;
    }

    public Class<Types> getTypes() {
        return Types.class;
    }

    public Class<Statements> getStatements() {
        return Statements.class;
    }
}
