package zzzank.probejs.docs;

import lombok.val;
import zzzank.probejs.ProbeJS;
import zzzank.probejs.features.kubejs.BindingFilter;
import zzzank.probejs.lang.java.ClassRegistry;
import zzzank.probejs.lang.typescript.ScriptDump;
import zzzank.probejs.lang.typescript.code.member.TypeDecl;
import zzzank.probejs.lang.typescript.code.ts.Statements;
import zzzank.probejs.lang.typescript.code.type.BaseType;
import zzzank.probejs.lang.typescript.code.type.Types;
import zzzank.probejs.plugin.ProbeJSPlugin;

/**
 * @author ZZZank
 */
public class LoadClassFn extends ProbeJSPlugin {

    @Override
    public void addGlobals(ScriptDump scriptDump) {
        setupClassPaths(scriptDump);
        val javaFn = Statements
            .func("java")
            .variable(Types.generic("T", Types.primitive("ClassPath")))
            .param("classPath", Types.generic("T"))
            .returnType(Types.parameterized(
                Types.primitive("LoadClass"),
                Types.generic("T")
            ))
            .build();

        val requireFn = Statements
            .func("require")
            .param("name", Types.STRING)
            .returnType(Types.ANY)
            .build();
        requireFn.addComment(
            "provided by ProbeJS, to support CommonJS style import/export",
            "",
            "You may have already noticed, classes dumped by ProbeJS have `packages/` before their TS module name.",
            "This is used for differentiating internal classes and variables exported by users.",
            "",
            "For `require(\"packages/abcdefg\")` style call, where parameter string starts with `packages/`, ProbeJS",
            "will try to locate corresponding java class, return located java class, or `undefined` if parameter",
            "does not match any java class or located Java class is forbidden by KubeJS/Rhino",
            "",
            "For `require(\"./abcdefg\")` style call, where parameter string does NOT start with `packages/`, this",
            "function call will be trimmed before being called. This is used for `export` support."
        );

        scriptDump.addGlobal("java", javaFn, requireFn);
    }

    @Override
    public void denyBindings(BindingFilter filter) {
        filter.denyFunction("java");
        filter.denyFunction("require");
    }

    private static void setupClassPaths(ScriptDump dump) {
        val paths = Types.object();
        for (val clazz : ClassRegistry.REGISTRY.foundClasses.values()) {
            val path = clazz.classPath;
            val typeOf = Types.typeOf(clazz.classPath);
            //original
            paths.member(clazz.original.getName(), typeOf);
            //probejs style import
            paths.member(path.getTypeScriptPath(), typeOf);
        }
        dump.addGlobal("load_class",
            new TypeDecl("GlobalClasses", Types.ignoreContext(paths.build(), BaseType.FormatType.RETURN)),
            new TypeDecl("ClassPath", Types.primitive("keyof GlobalClasses")),
            new TypeDecl("LoadClass<T>", Types.primitive("T extends ClassPath ? GlobalClasses[T] : never"))
        );
    }
}
