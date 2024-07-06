package moe.wolfgirl.probejs.docs.assignments;

import dev.latvian.mods.rhino.util.wrap.EnumTypeWrapper;
import lombok.val;
import moe.wolfgirl.probejs.lang.typescript.ScriptDump;
import moe.wolfgirl.probejs.plugin.ProbeJSPlugin;
import moe.wolfgirl.probejs.lang.typescript.code.type.BaseType;
import moe.wolfgirl.probejs.lang.typescript.code.type.Types;

import java.util.concurrent.locks.ReentrantLock;

public class EnumTypes extends ProbeJSPlugin {
    // EnumTypeWrapper is not thread-safe
    private static final ReentrantLock LOCK = new ReentrantLock();

    @Override
    public void assignType(ScriptDump scriptDump) {
        LOCK.lock();
        for (val recordedClass : scriptDump.recordedClasses) {
            if (!recordedClass.original.isEnum()) {
                continue;
            }
            try {
                val typeWrapper = EnumTypeWrapper.get(recordedClass.original);
                val types = typeWrapper.nameValues
                    .keySet()
                    .stream()
                    .map(Types::literal)
                    .toArray(BaseType[]::new);
                scriptDump.assignType(recordedClass.classPath, Types.or(types));
            } catch (Throwable ignore) {
            }
        }
        LOCK.unlock();
    }
}
