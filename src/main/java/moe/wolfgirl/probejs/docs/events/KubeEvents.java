package moe.wolfgirl.probejs.docs.events;

import lombok.val;
import moe.wolfgirl.probejs.features.kubejs.EventJSInfo;
import moe.wolfgirl.probejs.lang.transpiler.TypeConverter;
import moe.wolfgirl.probejs.lang.typescript.ScriptDump;
import moe.wolfgirl.probejs.lang.typescript.code.Code;
import moe.wolfgirl.probejs.lang.typescript.code.member.ParamDecl;
import moe.wolfgirl.probejs.lang.typescript.code.ts.FunctionDeclaration;
import moe.wolfgirl.probejs.lang.typescript.code.type.Types;
import moe.wolfgirl.probejs.plugin.ProbeJSPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class KubeEvents extends ProbeJSPlugin {

    public static final Map<String, EventJSInfo> KNOWN = new HashMap<>();

    @Override
    public void addGlobals(ScriptDump scriptDump) {

        val disabled = getSkippedEvents(scriptDump);
        val converter = scriptDump.transpiler.typeConverter;

        List<Code> codes = new ArrayList<>();
        for (val entry : KNOWN.entrySet()) {
            val id = entry.getKey();
            val info = entry.getValue();
            if (disabled.contains(id) || !info.scriptTypes().contains(scriptDump.scriptType)) {
                continue;
            }
            val decl = declareEventMethod(id, converter, info);
            decl.addComment(
                String.format(
                    "@at %s",
                    info.scriptTypes().stream().map(type -> type.name).collect(Collectors.joining(", "))
                ),
                String.format("@cancellable %s", info.cancellable() ? "Yes" : "No")
            );
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
        return KNOWN.values().stream().map(EventJSInfo::clazzRaw).collect(Collectors.toSet());
    }

    private static Set<String> getSkippedEvents(ScriptDump dump) {
        Set<String> events = new HashSet<>();
        ProbeJSPlugin.forEachPlugin(plugin -> events.addAll(plugin.disableEventDumps(dump)));
        return events;
    }
}
