package zzzank.probejs.docs.assignments;

import dev.latvian.mods.rhino.util.wrap.EnumTypeWrapper;
import lombok.val;
import zzzank.probejs.features.rhizo.RhizoState;
import zzzank.probejs.lang.java.clazz.Clazz;
import zzzank.probejs.lang.typescript.ScriptDump;
import zzzank.probejs.lang.typescript.code.type.BaseType;
import zzzank.probejs.lang.typescript.code.type.Types;
import zzzank.probejs.plugin.ProbeJSPlugin;

import java.util.concurrent.locks.ReentrantLock;

public class EnumTypes implements ProbeJSPlugin {
    // EnumTypeWrapper is not thread-safe
    private static final ReentrantLock LOCK = new ReentrantLock();

    @Override
    public void assignType(ScriptDump scriptDump) {
        if (!RhizoState.ENUM_TYPE_WRAPPER) {
            return;
        }
        LOCK.lock();
        for (val recordedClass : scriptDump.recordedClasses) {
            if (recordedClass.attribute.type != Clazz.ClassType.ENUM) {
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
