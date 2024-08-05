package zzzank.probejs.docs;

import lombok.val;
import zzzank.probejs.features.kubejs.BindingFilter;
import zzzank.probejs.lang.typescript.ScriptDump;
import zzzank.probejs.lang.typescript.code.ts.Statements;
import zzzank.probejs.lang.typescript.code.type.Types;
import zzzank.probejs.plugin.ProbeJSPlugin;

/**
 * @author ZZZank
 */
public class LoadClassFn extends ProbeJSPlugin {

    @Override
    public void addGlobals(ScriptDump scriptDump) {
        val fn = Statements
            .func("java")
            .param("className", Types.STRING)
            .returnType(Types.ANY)
            .build();

        fn.addComment(
            "@deprecated",
            "Please use `require(...)` instead."
        );

        scriptDump.addGlobal("java", fn);
    }

    @Override
    public void denyBindings(BindingFilter filter) {
        filter.denyFunction("java");
        filter.denyFunction("require");
    }
}
