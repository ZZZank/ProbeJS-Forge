package zzzank.probejs.plugin;

import dev.latvian.kubejs.script.ScriptManager;
import dev.latvian.mods.rhino.*;
import dev.latvian.mods.rhino.util.HideFromJS;
import lombok.val;
import zzzank.probejs.features.rhizo.RemapperBridge;
import zzzank.probejs.lang.java.clazz.ClassPath;
import zzzank.probejs.utils.GameUtils;

public class Require extends BaseFunction {
    private final ScriptManager manager;

    public Require(ScriptManager manager) {
        this.manager = manager;
    }

    @Override
    public Object call(Context cx, Scriptable scope, Scriptable self, Object[] args) {
        String name;
        if (args.length == 0 || !(name = args[0].toString()).startsWith(ClassPath.TS_PATH_PREFIX)) {
            return new RequireWrapper(null, Undefined.instance);
        }
        val path = ClassPath.fromTSPath(name);

        try {
            return new RequireWrapper(path, manager.loadJavaClass(scope, new String[]{name}));
        } catch (Exception ignored) {
            manager.type.console.warn(String.format("Class '%s' not loaded", path.getClassPathJava()));
            return new RequireWrapper(path, Undefined.instance);
        }
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
