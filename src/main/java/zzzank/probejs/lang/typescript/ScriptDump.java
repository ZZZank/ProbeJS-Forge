package zzzank.probejs.lang.typescript;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.KubeJSPaths;
import dev.latvian.kubejs.script.ScriptManager;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.server.ServerScriptManager;
import dev.latvian.kubejs.util.UtilsJS;
import lombok.val;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.io.FileUtils;
import zzzank.probejs.ProbeJS;
import zzzank.probejs.ProbePaths;
import zzzank.probejs.api.output.PackagedWriter;
import zzzank.probejs.api.output.PerFileWriter;
import zzzank.probejs.api.output.TSFileWriter;
import zzzank.probejs.lang.java.clazz.ClassPath;
import zzzank.probejs.lang.java.clazz.Clazz;
import zzzank.probejs.lang.transpiler.Transpiler;
import zzzank.probejs.lang.typescript.code.Code;
import zzzank.probejs.lang.typescript.code.member.ClassDecl;
import zzzank.probejs.lang.typescript.code.member.TypeDecl;
import zzzank.probejs.lang.typescript.code.ts.Wrapped;
import zzzank.probejs.lang.typescript.code.type.BaseType;
import zzzank.probejs.lang.typescript.code.type.Types;
import zzzank.probejs.lang.typescript.code.type.js.JSJoinedType;
import zzzank.probejs.lang.typescript.refer.ImportType;
import zzzank.probejs.plugin.ProbeJSPlugins;
import zzzank.probejs.utils.CollectUtils;
import zzzank.probejs.utils.GameUtils;
import zzzank.probejs.utils.JsonUtils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Controls a dump. A dump is made of a script type, and is responsible for
 * maintaining the file structures
 */
public class ScriptDump {
    public static final Supplier<ScriptDump> SERVER_DUMP = () -> {
        ServerScriptManager scriptManager = ServerScriptManager.instance;
        if (scriptManager == null) {
            return null;
        }

        return new ScriptDump(
            scriptManager.scriptManager,
            ProbePaths.PROBE.resolve("server"),
            KubeJSPaths.SERVER_SCRIPTS,
            clazz -> clazz.getAnnotations(OnlyIn.class)
                .stream()
                .noneMatch(annotation -> annotation.value().isClient())
        );
    };

    public static final Supplier<ScriptDump> CLIENT_DUMP = () -> new ScriptDump(
        KubeJS.clientScriptManager,
        ProbePaths.PROBE.resolve("client"),
        KubeJSPaths.CLIENT_SCRIPTS,
        clazz -> clazz.getAnnotations(OnlyIn.class)
            .stream()
            .noneMatch(annotation -> annotation.value().isDedicatedServer())
    );

    public static final Supplier<ScriptDump> STARTUP_DUMP = () -> new ScriptDump(
        KubeJS.startupScriptManager,
        ProbePaths.PROBE.resolve("startup"),
        KubeJSPaths.STARTUP_SCRIPTS,
        (clazz -> true)
    );

    public static final String SIMPLE_PACKAGE = "simple_package_classes";

    public final ScriptType scriptType;
    public final ScriptManager manager;
    public final Path basePath;
    public final Path scriptPath;
    public final Map<String, TypeScriptFile> globals = new HashMap<>();
    public final Transpiler transpiler;
    public final Set<Clazz> recordedClasses = new HashSet<>();
    private final Predicate<Clazz> accept;
    private final Multimap<ClassPath, TypeDecl> convertibles = ArrayListMultimap.create();

    public final TSFileWriter classesWriter = new PackagedWriter(2, SIMPLE_PACKAGE);
    public final TSFileWriter globalWriter = new PerFileWriter().setWithIndex(false).setWriteAsModule(false);

    public ScriptDump(ScriptManager manager, Path basePath, Path scriptPath, Predicate<Clazz> scriptPredicate) {
        this.scriptType = manager.type;
        this.manager = manager;
        this.basePath = basePath;
        this.scriptPath = scriptPath;
        this.transpiler = new Transpiler(manager);
        this.accept = scriptPredicate;

//        val pack = CollectUtils.anyIn(manager.packs.values());
//        this.attachedContext = pack.context;
//        this.attachedScope = pack.scope;
    }

    public void acceptClasses(Collection<Clazz> classes) {
        for (Clazz clazz : classes) {
            if (accept.test(clazz)) {
                recordedClasses.add(clazz);
            }
        }
    }

    public Set<Class<?>> retrieveClasses() {
        Set<Class<?>> classes = new HashSet<>();
        ProbeJSPlugins.forEachPlugin(plugin -> classes.addAll(plugin.provideJavaClass(this)));
        return classes;
    }

    public void assignType(Class<?> classPath, BaseType type) {
        assignType(ClassPath.fromJava(classPath), type);
    }

    public void assignType(ClassPath classPath, BaseType type) {
        convertibles.put(classPath, new TypeDecl(null, type));
    }

    public void assignType(Class<?> classPath, String name, BaseType type) {
        assignType(ClassPath.fromJava(classPath), name, type);
    }

    public void assignType(ClassPath classPath, String name, BaseType type) {
        convertibles.put(classPath, new TypeDecl(name, type));
    }

    public void addGlobal(String identifier, Code... content) {
        addGlobal(identifier, Collections.emptyList(), content);
    }

    public void addGlobal(String identifier, Collection<String> excludedNames, Code... content) {
        val file = globals.computeIfAbsent(
            identifier,
            k -> new TypeScriptFile(ClassPath.fromRaw(k))
        );

        for (val excluded : excludedNames) {
            file.excludeSymbol(excluded);
        }

        val global = new Wrapped.Global();
        for (val code : content) {
            global.addCode(code);
        }
        file.addCode(global);
    }

    public Path ensurePath(String path) {
        return ensurePath(path, false);
    }

    public Path ensurePath(String path, boolean script) {
        Path full = (script ? scriptPath : basePath).resolve(path);
        if (Files.notExists(full)) {
            UtilsJS.tryIO(() -> Files.createDirectories(full));
        }
        return full;
    }

    public Path getTypeFolder() {
        return ensurePath("probe-types");
    }

    public Path getPackageFolder() {
        return ensurePath("probe-types/packages");
    }

    public Path getGlobalFolder() {
        return ensurePath("probe-types/global");
    }

    public Path getSource() {
        return ensurePath("src", true);
    }

    public Path getTest() {
        return ensurePath("test", true);
    }

    public void dumpClasses() throws IOException {
        transpiler.init();
        ProbeJSPlugins.forEachPlugin(plugin -> plugin.assignType(this));

        Map<String, BufferedWriter> files = new HashMap<>();
        Map<ClassPath, TypeScriptFile> globalClasses = transpiler.dump(recordedClasses);
        ProbeJSPlugins.forEachPlugin(plugin -> plugin.modifyClasses(this, globalClasses));

        for (val entry : globalClasses.entrySet()) {
            try {
                val classPath = entry.getKey();
                val output = entry.getValue();
                val classDecl = output.findCode(ClassDecl.class).orElse(null);
                if (classDecl == null) {
                    continue;
                }

                // Add all assignable types
                // type ExportedType = ConvertibleTypes
                // declare global {
                //     type Type_ = ExportedType
                // }
                val generics = CollectUtils.mapToList(classDecl.variableTypes, v -> v.symbol);
                String symbol = classPath.getName() + "_";
                String exportedSymbol = ImportType.TYPE.fmt(classPath.getName());
                BaseType exportedType = Types.type(classPath);
                BaseType thisType = Types.type(classPath);

                if (!generics.isEmpty()) {
                    val suffix = "<" + String.join(", ", generics) + ">";
                    symbol = symbol + suffix;
                    exportedSymbol = exportedSymbol + suffix;
                    val genericParams = generics.stream()
                        .map(Types::generic)
                        .toArray(BaseType[]::new);
                    thisType = Types.parameterized(thisType, genericParams);
                    exportedType = Types.parameterized(exportedType, genericParams);
                }

                exportedType = Types.contextShield(exportedType, BaseType.FormatType.INPUT);
                thisType = Types.contextShield(thisType, BaseType.FormatType.RETURN);

                List<BaseType> allTypes = new ArrayList<>();
                List<TypeDecl> delegatedTypes = new ArrayList<>();
                for (TypeDecl typeDecl : convertibles.get(classPath)) {
                    if (typeDecl.symbol == null) {
                        allTypes.add(typeDecl.type);
                    } else {
                        delegatedTypes.add(typeDecl);
                        allTypes.add(Types.primitive(typeDecl.symbol));
                    }
                }

                allTypes.add(thisType);

                val convertibleType = new TypeDecl(exportedSymbol, new JSJoinedType.Union(allTypes));
                val globalType = new TypeDecl(symbol, exportedType);
                val typeExport = new Wrapped.Global();
                typeExport.addCode(globalType);
                convertibleType.addComment("""
                    Class-specific type exported by ProbeJS, use global `{Type}_` types for convenience unless there's a naming conflict.
                    """);
                typeExport.addComment("""
                    Global type exported for convenience, use class-specific types if there's a naming conflict.
                    """);
                for (TypeDecl delegatedType : delegatedTypes) {
                    output.addCode(delegatedType);
                }
                output.addCode(convertibleType);
                output.addCode(typeExport);

                classesWriter.accept(output);
            } catch (Throwable t) {
                GameUtils.logThrowable(t);
            }
        }

        try {
            classesWriter.write(getPackageFolder());
        } catch (IOException e) {
            GameUtils.logThrowable(e);
        }
    }

    public void dumpGlobal() throws IOException {
        ProbeJSPlugins.forEachPlugin(plugin -> plugin.addGlobals(this));

        for (val file : globals.values()) {
            globalWriter.accept(file);
        }

        try (val writer = Files.newBufferedWriter(getGlobalFolder().resolve("index.d.ts"))) {
            for (val identifier : globals.keySet()) {
                writer.write(String.format("export * from %s\n", ProbeJS.GSON.toJson("./" + identifier)));
            }
        }
        globalWriter.write(getGlobalFolder());
    }

    public void dumpJSConfig() throws IOException {
        val config = (JsonObject) JsonUtils.parseObject(
            CollectUtils.ofMap(
                "compilerOptions", CollectUtils.ofMap(
                    "module", "commonjs",
                    "target", "ES2015",
                    "lib", CollectUtils.ofList("ES5", "ES2015"),
                    "rootDir", ".",
                    "typeRoots", CollectUtils.ofList(String.format("../../.probe/%s/probe-types", basePath.getFileName())),
                    "baseUrl", String.format("../../.probe/%s/probe-types", basePath.getFileName()),
                    "skipLibCheck", true
                ),
                "include", CollectUtils.ofList("./**/*.js")
            )
        );
        zzzank.probejs.utils.FileUtils.writeMergedConfig(scriptPath.resolve("jsconfig.json"), config);
    }

    public void removeClasses() throws IOException {
        FileUtils.deleteDirectory(getTypeFolder().toFile());
    }

    public void dump() throws IOException, ClassNotFoundException {
//        getSource();
//        getTest();

        dumpClasses();
        dumpGlobal();
        dumpJSConfig();
    }

    private static void write(Path writeTo, String content) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(writeTo)) {
            writer.write(content);
        }
    }
}
