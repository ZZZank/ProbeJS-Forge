package moe.wolfgirl.probejs.docs.events;


import moe.wolfgirl.probejs.GlobalStates;
import moe.wolfgirl.probejs.plugin.ProbeJSPlugin;
import moe.wolfgirl.probejs.lang.typescript.ScriptDump;
import net.minecraftforge.eventbus.api.Event;


import java.util.HashSet;
import java.util.Set;

public class ForgeEvents extends ProbeJSPlugin {

//    @Override
//    public void modifyClasses(ScriptDump scriptDump, Map<ClassPath, TypeScriptFile> globalClasses) {
//        TypeScriptFile typeScriptFile = globalClasses.get(new ClassPath(NativeEventWrapper.class));
//        typeScriptFile.declaration.addClass(new ClassPath(Event.class));
//        ClassDecl classDecl = typeScriptFile.findCode(ClassDecl.class).orElse(null);
//        if (classDecl == null) {
//            return;
//        }
//
//        for (MethodDecl method : classDecl.methods) {
//            if (method.name.equals("onEvent")) {
//                method.variableTypes.add(
//                    Types.generic(
//                        "T",
//                        Types.typeOf(
//                            Types.parameterized(
//                                Types.type(Event.class),
//                                Types.UNKNOWN
//                            )
//                        )
//                    )
//                );
//                if (method.params.getFirst().name.equals("priority")) {
//                    method.params.get(1).type = Types.generic("T");
//                    method.params.get(2).type = Types.lambda()
//                        .param("event", Types.parameterized(Types.primitive("InstanceType"), Types.primitive("T")))
//                        .build();
//                } else {
//                    method.params.get(0).type = Types.generic("T");
//                    method.params.get(1).type = Types.lambda()
//                        .param("event", Types.parameterized(Types.primitive("InstanceType"), Types.primitive("T")))
//                        .build();
//                }
//            }
//        }
//    }

    @Override
    public Set<Class<?>> provideJavaClass(ScriptDump scriptDump) {
        Set<Class<?>> classes = new HashSet<>(GlobalStates.KNOWN_EVENTS);
        classes.add(Event.class);
        return classes;
    }
}
