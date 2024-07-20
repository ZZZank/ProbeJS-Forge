package zzzank.probejs.docs.events;


import dev.latvian.kubejs.script.ScriptType;
import lombok.val;
import net.minecraftforge.eventbus.api.Event;
import zzzank.probejs.GlobalStates;
import zzzank.probejs.lang.typescript.ScriptDump;
import zzzank.probejs.lang.typescript.code.ts.Statements;
import zzzank.probejs.lang.typescript.code.type.Types;
import zzzank.probejs.plugin.ProbeJSPlugin;

import java.util.HashSet;
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
                    .param("event", Types.instanceType(Types.primitive(T)))
                    .returnType(Types.VOID)
                    .build()
            )
            .returnType(Types.VOID)
            .build());
    }

    @Override
    public Set<Class<?>> provideJavaClass(ScriptDump scriptDump) {
        Set<Class<?>> classes = new HashSet<>(GlobalStates.KNOWN_EVENTS);
        classes.add(Event.class);
        return classes;
    }
}
