package zzzank.probejs.docs;

import lombok.val;
import zzzank.probejs.features.kubejs.BindingFilter;
import zzzank.probejs.lang.java.ClassRegistry;
import zzzank.probejs.lang.typescript.ScriptDump;
import zzzank.probejs.lang.typescript.code.member.TypeDecl;
import zzzank.probejs.lang.typescript.code.ts.Statements;
import zzzank.probejs.lang.typescript.code.type.BaseType;
import zzzank.probejs.lang.typescript.code.type.Types;
import zzzank.probejs.lang.typescript.refer.ImportInfo;
import zzzank.probejs.lang.typescript.refer.ImportInfos;
import zzzank.probejs.lang.typescript.refer.ImportType;
import zzzank.probejs.plugin.ProbeJSPlugin;

/**
 * @author ZZZank
 */
public class LoadClassFn implements ProbeJSPlugin {

    @Override
    public void addGlobals(ScriptDump scriptDump) {
        val classTypeBase = Types.type(Class.class);
        val paths = Types.object();
        for (val clazz : ClassRegistry.REGISTRY.foundClasses.values()) {
            val path = clazz.classPath;
            val typeOf = Types.and(
                Types.typeOf(clazz.classPath), //typeof A, and
                Types.parameterized(classTypeBase, Types.type(path)) //Class<A>
            );
            //original
            paths.member(clazz.original.getName(), typeOf);
            //probejs style import
            paths.member(path.getTSPath(), typeOf);
        }
        scriptDump.addGlobal(
            "load_class",
            new TypeDecl(
                "GlobalClasses",
                paths.build()
                    .contextShield(BaseType.FormatType.RETURN)
                    .importShield(ImportInfos.of(ClassRegistry.REGISTRY.foundClasses.values()
                        .stream()
                        .map(c -> c.classPath)
                        .map(path -> ImportInfo.of(path, ImportType.ORIGINAL)))
                    )
            ),
            new TypeDecl("ClassPath", Types.primitive("keyof GlobalClasses")),
            new TypeDecl("LoadClass<T>", Types.primitive("T extends ClassPath ? GlobalClasses[T] : never"))
        );

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
            "@deprecated please use `java()` directly ProbeJS adds TS path support for it.",
            "@see java"
        );

        scriptDump.addGlobal("java", javaFn, requireFn);
    }

    @Override
    public void denyBindings(BindingFilter filter) {
        filter.denyFunction("java");
        filter.denyFunction("require");
    }
}
