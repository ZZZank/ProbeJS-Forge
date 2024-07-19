package moe.wolfgirl.probejs.docs.events;


import dev.latvian.kubejs.script.ScriptType;
import lombok.val;
import moe.wolfgirl.probejs.GlobalStates;
import moe.wolfgirl.probejs.lang.typescript.code.member.ParamDecl;
import moe.wolfgirl.probejs.lang.typescript.code.ts.FunctionDeclaration;
import moe.wolfgirl.probejs.lang.typescript.code.type.TSVariableType;
import moe.wolfgirl.probejs.lang.typescript.code.type.Types;
import moe.wolfgirl.probejs.plugin.ProbeJSPlugin;
import moe.wolfgirl.probejs.lang.typescript.ScriptDump;
import net.minecraftforge.eventbus.api.Event;


import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ForgeEvents extends ProbeJSPlugin {
    @Override
    public void addGlobals(ScriptDump scriptDump) {
        if (scriptDump.scriptType != ScriptType.STARTUP) {
            return;
        }

        val T = "T";

        scriptDump.addGlobal("forge_events", new FunctionDeclaration(
            "onForgeEvent",
            Collections.singletonList(new TSVariableType(T, Types.typeOf(Event.class))),
            Arrays.asList(
                new ParamDecl("target", Types.generic(T)),
                new ParamDecl(
                    "handler",
                    Types.lambda()
                        .param("event", Types.parameterized(Types.primitive("InstanceType"), Types.primitive(T)))
                        .returnType(Types.VOID)
                        .build()
                )
            ),
            Types.VOID
        ));
    }

    @Override
    public Set<Class<?>> provideJavaClass(ScriptDump scriptDump) {
        Set<Class<?>> classes = new HashSet<>(GlobalStates.KNOWN_EVENTS);
        classes.add(Event.class);
        return classes;
    }
}
