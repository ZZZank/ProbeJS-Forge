package zzzank.probejs.docs.events;

import lombok.val;
import org.jetbrains.annotations.NotNull;
import zzzank.probejs.features.kubejs.EventJSInfo;
import zzzank.probejs.features.kubejs.EventJSInfos;
import zzzank.probejs.lang.transpiler.TypeConverter;
import zzzank.probejs.lang.typescript.ScriptDump;
import zzzank.probejs.lang.typescript.code.Code;
import zzzank.probejs.lang.typescript.code.member.ParamDecl;
import zzzank.probejs.lang.typescript.code.ts.FunctionDeclaration;
import zzzank.probejs.lang.typescript.code.type.Types;
import zzzank.probejs.plugin.ProbeJSPlugin;

import java.util.*;
import java.util.stream.Collectors;

public class KubeEvents extends ProbeJSPlugin {

    @Override
    public void addGlobals(ScriptDump scriptDump) {

        val disabled = getSkippedEvents(scriptDump);
        val converter = scriptDump.transpiler.typeConverter;

        List<Code> codes = new ArrayList<>();
        for (val info : EventJSInfos.sortedInfos()) {
            val id = info.id();
            if (disabled.contains(id) || !info.scriptTypes().contains(scriptDump.scriptType)) {
                continue;
            }
            val decl = declareEventMethod(id, converter, info);
            decl.addComment(String.format(
                """
                    @at %s
                    @cancellable %s
                    """,
                info.scriptTypes().stream().map(type -> type.name).collect(Collectors.joining(", ")),
                info.cancellable() ? "Yes" : "No"
            ));
            if (info.hasSub()) {
                decl.addComment(String.format(
                    "This event provides sub-event variant, e.g. `%s.%s`",
                    id,
                    info.sub().getValue()
                ));
                codes.add(declareEventMethod(id + ".${string}", converter, info));
            }
            codes.add(decl);
        }

        scriptDump.addGlobal("events", codes.toArray(new Code[0]));
    }

    private static @NotNull FunctionDeclaration declareEventMethod(String id, TypeConverter converter, EventJSInfo info) {
        return new FunctionDeclaration(
            "onEvent",
            Collections.emptyList(),
            Arrays.asList(
                new ParamDecl("id", Types.literal(id)),
                new ParamDecl(
                    "handler",
                    Types.lambda()
                        .param("event", converter.convertType(info.clazzRaw()))
                        .returnType(Types.VOID)
                        .build()
                )
            ),
            Types.VOID
        );
    }

    @Override
    public Set<Class<?>> provideJavaClass(ScriptDump scriptDump) {
        return EventJSInfos.provideClasses();
    }

    private static Set<String> getSkippedEvents(ScriptDump dump) {
        Set<String> events = new HashSet<>();
        ProbeJSPlugin.forEachPlugin(plugin -> events.addAll(plugin.disableEventDumps(dump)));
        return events;
    }
}
