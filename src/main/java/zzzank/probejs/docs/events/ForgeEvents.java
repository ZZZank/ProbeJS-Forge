package zzzank.probejs.docs.events;


import dev.latvian.kubejs.script.ScriptType;
import lombok.val;
import net.minecraftforge.eventbus.api.Event;
import zzzank.probejs.features.kubejs.BindingFilter;
import zzzank.probejs.lang.typescript.ScriptDump;
import zzzank.probejs.lang.typescript.code.ts.Statements;
import zzzank.probejs.lang.typescript.code.type.Types;
import zzzank.probejs.lang.typescript.code.type.utility.TSUtilityType;
import zzzank.probejs.plugin.ProbeJSPlugin;

import java.util.Collections;
import java.util.Set;

public class ForgeEvents extends ProbeJSPlugin {
    @Override
    public void addGlobals(ScriptDump scriptDump) {
        if (scriptDump.scriptType != ScriptType.STARTUP) {
            return;
        }

        val T = "T";

        scriptDump.addGlobal("forge_events", Statements
            .func("onForgeEvent")
            .variable(Types.generic(T, Types.typeOf(Event.class)))
            .param("target", Types.generic(T))
            .param(
                "handler",
                Types.lambda()
                    .param("event", TSUtilityType.instanceType(Types.primitive(T)))
                    .build()
            )
            .build());
    }

    @Override
    public Set<Class<?>> provideJavaClass(ScriptDump scriptDump) {
        return Collections.singleton(Event.class);
    }

    @Override
    public void denyBindings(BindingFilter filter) {
        filter.denyFunction("onForgeEvent");
    }
}
