package zzzank.probejs.events;

import dev.latvian.kubejs.event.EventJS;
import zzzank.probejs.lang.typescript.ScriptDump;
import zzzank.probejs.lang.typescript.code.type.BaseType;

public class TypeAssignmentEventJS extends EventJS {
    private final ScriptDump scriptDump;

    public TypeAssignmentEventJS(ScriptDump scriptDump) {
        this.scriptDump = scriptDump;
    }

    public void assignType(Class<?> clazz, BaseType baseType) {
        scriptDump.assignType(clazz, baseType);
    }
}
