package zzzank.probejs.docs.bindings;

import dev.latvian.kubejs.BuiltinKubeJSPlugin;
import lombok.val;
import zzzank.probejs.lang.typescript.ScriptDump;
import zzzank.probejs.lang.typescript.code.ts.Statements;
import zzzank.probejs.lang.typescript.code.type.Types;
import zzzank.probejs.lang.typescript.code.type.js.JSPrimitiveType;
import zzzank.probejs.plugin.ProbeJSPlugin;

/**
 * resolve values in global, but keep in mind that only first level members are resolved
 *
 * @author ZZZank
 */
class ResolveGlobal extends ProbeJSPlugin {

    public static final String NAME = "global";
    public static final JSPrimitiveType RESOLVED = Types.primitive("ProbeJS$$ResolvedGlobal");

    ResolveGlobal() {
    }

    @Override
    public void addGlobals(ScriptDump scriptDump) {
        val clazzDecl = Statements.clazz(RESOLVED.content);

        for (val entry : BuiltinKubeJSPlugin.GLOBAL.entrySet()) {
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
