package zzzank.probejs.docs;

import lombok.val;
import zzzank.probejs.lang.java.ClassRegistry;
import zzzank.probejs.lang.java.clazz.ClassPath;
import zzzank.probejs.lang.typescript.ScriptDump;
import zzzank.probejs.lang.typescript.code.member.TypeDecl;
import zzzank.probejs.lang.typescript.code.type.BaseType;
import zzzank.probejs.lang.typescript.code.type.Types;
import zzzank.probejs.lang.typescript.code.type.js.JSPrimitiveType;
import zzzank.probejs.lang.typescript.code.type.utility.TSUtilityType;
import zzzank.probejs.lang.typescript.refer.ImportInfo;
import zzzank.probejs.lang.typescript.refer.ImportInfos;
import zzzank.probejs.plugin.ProbeJSPlugin;

/**
 * @author ZZZank
 */
public class GlobalClassPaths implements ProbeJSPlugin {
    public static final JSPrimitiveType CLASS_PATH = Types.primitive("ClassPath");
    public static final JSPrimitiveType JAVA_CLASS_PATH = Types.primitive("JavaClassPath");
    public static final JSPrimitiveType TS_CLASS_PATH = Types.primitive("TSClassPath");
    public static final JSPrimitiveType GLOBAL_CLASSES = Types.primitive("GlobalClasses");

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

        val classPathTemplate = Types.primitive(String.format("`%s${string}`", ClassPath.TS_PATH_PREFIX));
        scriptDump.addGlobal(
            "load_class",
            new TypeDecl(
                GLOBAL_CLASSES.content,
                paths.build()
                    .contextShield(BaseType.FormatType.RETURN)
                    .importShield(ImportInfos.of(ClassRegistry.REGISTRY.foundClasses.values()
                        .stream()
                        .map(c -> c.classPath)
                        .map(ImportInfo::ofOriginal))
                    )
            ),
            new TypeDecl(CLASS_PATH.content, Types.STRING.and(Types.primitive("keyof GlobalClasses"))),
            new TypeDecl(JAVA_CLASS_PATH.content, TSUtilityType.exclude(CLASS_PATH, classPathTemplate)),
            new TypeDecl(TS_CLASS_PATH.content, TSUtilityType.extract(CLASS_PATH, classPathTemplate)),
            new TypeDecl("LoadClass<T>", Types.primitive("T extends ClassPath ? GlobalClasses[T] : never"))
        );
    }
}
