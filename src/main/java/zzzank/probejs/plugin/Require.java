package zzzank.probejs.plugin;

import dev.latvian.kubejs.script.ScriptManager;
import dev.latvian.mods.rhino.*;
import dev.latvian.mods.rhino.util.HideFromJS;
import lombok.val;
import zzzank.probejs.lang.java.clazz.ClassPath;

import java.util.Arrays;
import java.util.stream.Collectors;

//TODO: replace with js file for full backward compat
public class Require extends BaseFunction {
    private final ScriptManager manager;

    public Require(ScriptManager manager) {
        this.manager = manager;
    }

    @Override
    public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        val result = (String) Context.jsToJava(args[0], String.class);
        if (!result.startsWith("packages")) {
            return new RequireWrapper(null, Undefined.instance);
        }
        val parts = result.split("/", 2);
        val path = new ClassPath(Arrays.stream(parts[1].split("/")).collect(Collectors.toList()));

        NativeJavaClass loaded = null;
        try {
            loaded = manager.loadJavaClass(scope, new String[]{path.getClassPathJava()});
        } catch (Exception ignored) {
        }
        if (loaded == null) {
            manager.type.console.warn(String.format("Class '%s' not loaded", path.getClassPathJava()));
            return new RequireWrapper(path, Undefined.instance);
        }
        return new RequireWrapper(path, loaded);
    }

    public static class RequireWrapper extends ScriptableObject {
        private final ClassPath path;
        private final Object clazz;

        public RequireWrapper(ClassPath path, Object clazz) {
            assert clazz == Undefined.instance || clazz instanceof NativeJavaClass;
            this.path = path;
            this.clazz = clazz;
        }

        @Override
        public String getClassName() {
            return path.getClassPathJava();
        }

        @Override
        public Object get(String name, Scriptable start) {
            if (path == null || name.equals(path.getName())) {
                return clazz;
            }
            return super.get(name, start);
        }

        @HideFromJS
        public ClassPath getPath() {
            return path;
        }
    }
}
