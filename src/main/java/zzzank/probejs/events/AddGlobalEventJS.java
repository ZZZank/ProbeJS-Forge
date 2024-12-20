package zzzank.probejs.events;

import dev.latvian.kubejs.event.EventJS;
import zzzank.probejs.lang.typescript.ScriptDump;
import zzzank.probejs.lang.typescript.code.Code;
import zzzank.probejs.lang.typescript.code.ts.Statements;

import java.util.Arrays;

public class AddGlobalEventJS extends EventJS {
    private final ScriptDump scriptDump;

    public AddGlobalEventJS(ScriptDump scriptDump) {
        this.scriptDump = scriptDump;
    }

    public void addGlobal(String identifier, Code... content) {
        scriptDump.addGlobal(identifier, content);
    }

    public void addGlobal(String identifier, String[] excludedNames, Code... content) {
        scriptDump.addGlobal(identifier, Arrays.asList(excludedNames), content);
    }

    public Class<Statements> getTypeOfStatements() {
        return Statements.class;
    }
}
