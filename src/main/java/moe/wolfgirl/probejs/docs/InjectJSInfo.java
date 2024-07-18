package moe.wolfgirl.probejs.docs;

import lombok.val;
import moe.wolfgirl.probejs.lang.java.clazz.ClassPath;
import moe.wolfgirl.probejs.lang.typescript.ScriptDump;
import moe.wolfgirl.probejs.lang.typescript.TypeScriptFile;
import moe.wolfgirl.probejs.plugin.ProbeJSPlugin;

import java.util.Map;

/**
 * @author ZZZank
 */
public class InjectJSInfo extends ProbeJSPlugin {

    @Override
    public void modifyClasses(ScriptDump scriptDump, Map<ClassPath, TypeScriptFile> globalClasses) {
        for (TypeScriptFile tsFile : globalClasses.values()) {
            val className = tsFile.classPath.getClassPath();
            try {
                val c = Class.forName(className);
            } catch (ClassNotFoundException e) {

            }
        }
    }
}
