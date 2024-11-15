package zzzank.probejs.docs.bindings;

import dev.latvian.kubejs.BuiltinKubeJSPlugin;
import lombok.val;
import zzzank.probejs.lang.transpiler.TypeConverter;
import zzzank.probejs.lang.typescript.ScriptDump;
import zzzank.probejs.lang.typescript.code.ts.Statements;
import zzzank.probejs.lang.typescript.code.type.BaseType;
import zzzank.probejs.lang.typescript.code.type.Types;
import zzzank.probejs.lang.typescript.code.type.js.JSPrimitiveType;

/**
 * resolve values in global, but keep in mind that only first level members are resolved
 *
 * @author ZZZank
 */
class ResolveGlobal {

    public static final String NAME = "global";
    public static final JSPrimitiveType RESOLVED = Types.primitive("ProbeJS$$ResolvedGlobal");

    public static void addGlobals(ScriptDump scriptDump) {
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

    public static BaseType resolveType(int depth, Object value, TypeConverter converter) {
        if (value == null) {
            return Types.NULL;
        }
        val directType = converter.convertType(value.getClass());
        if (depth < 1) {
            return directType;
        }
        val resolved = ValueTypes.convert(value, converter, depth);
        throw new AssertionError();
    }
}
