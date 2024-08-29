package zzzank.probejs.docs.bindings;

import lombok.val;
import zzzank.probejs.lang.typescript.ScriptDump;
import zzzank.probejs.lang.typescript.code.ts.Statements;
import zzzank.probejs.lang.typescript.code.type.Types;
import zzzank.probejs.lang.typescript.code.type.js.JSPrimitiveType;
import zzzank.probejs.plugin.ProbeJSPlugin;

import java.util.HashMap;

/**
 * @author ZZZank
 */
class ResolveGlobal extends ProbeJSPlugin {

    static final String NAME = "global";
    static final JSPrimitiveType RESOLVED = Types.primitive("ProbeJS$$ResolvedGlobal");
    private final DummyBindingEvent event;

    ResolveGlobal(DummyBindingEvent bindingEvent) {
        this.event = bindingEvent;
    }

    @Override
    public void addGlobals(ScriptDump scriptDump) {
        val clazzDecl = Statements.clazz(RESOLVED.content);

        val map = (HashMap<?, ?>) event.constants.get(NAME);
        for (val entry : map.entrySet()) {
            val name = String.valueOf(entry.getKey());
            val value = entry.getValue();
            if (value == null) {
                continue;
            }
            val type = scriptDump.transpiler.typeConverter.convertType(value.getClass());
            clazzDecl.field(name, type);
        }

        scriptDump.addGlobal("resolved_global", clazzDecl.build());
    }
}
