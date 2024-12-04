package zzzank.probejs.lang.transpiler;

import dev.latvian.mods.rhino.util.HideFromJS;
import lombok.val;
import zzzank.probejs.lang.java.clazz.ClassPath;
import zzzank.probejs.lang.java.clazz.Clazz;
import zzzank.probejs.lang.transpiler.transformation.ClassTransformer;
import zzzank.probejs.lang.typescript.ScriptDump;
import zzzank.probejs.lang.typescript.TypeScriptFile;
import zzzank.probejs.plugin.ProbeJSPlugins;

import java.util.*;

/**
 * Converts a Clazz into a TypeScriptFile ready for dump.
 */
public class Transpiler {
    public final TypeConverter typeConverter;
    public final Set<ClassPath> rejectedClasses = new HashSet<>();
    private final ScriptDump scriptDump;

    public Transpiler(ScriptDump scriptDump) {
        this.scriptDump = scriptDump;
        this.typeConverter = new TypeConverter();
    }

    public void reject(Class<?> clazz) {
        rejectedClasses.add(ClassPath.fromJava(clazz));
    }

    public void init() {
        ProbeJSPlugins.forEachPlugin(plugin -> {
            plugin.addPredefinedTypes(typeConverter);
            plugin.denyTypes(this);
        });
    }

    public Map<ClassPath, TypeScriptFile> dump(Collection<Clazz> clazzes) {
        val transpiler = new ClassTranspiler(
            typeConverter,
            ClassTransformer.fromPlugin(scriptDump, this)
        );
        Map<ClassPath, TypeScriptFile> result = new HashMap<>();

        for (val clazz : clazzes) {
            if (rejectedClasses.contains(clazz.classPath) || clazz.hasAnnotation(HideFromJS.class)) {
                continue;
            }

            val classDecl = transpiler.transpile(clazz);
            val scriptFile = new TypeScriptFile(clazz.classPath);
            scriptFile.addCode(classDecl);

            result.put(clazz.classPath, scriptFile);
        }

        return result;
    }
}
