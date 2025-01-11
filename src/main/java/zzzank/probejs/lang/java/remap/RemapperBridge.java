package zzzank.probejs.lang.java.remap;

import lombok.experimental.UtilityClass;
import zzzank.probejs.features.rhizo.RhizoClazzRemapper;
import zzzank.probejs.features.rhizo.RhizoState;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author ZZZank
 */
@UtilityClass
public class RemapperBridge {
    private ClazzNamesRemapper INSTANCE;

    static {
        if (RhizoState.REMAPPER) {
            INSTANCE = new RhizoClazzRemapper();
        } else {
            INSTANCE = new RhinoDefault();
        }
    }

    public void set(ClazzNamesRemapper remapper) {
        INSTANCE = Objects.requireNonNull(remapper);
    }

    public ClazzNamesRemapper get() {
        return INSTANCE;
    }

    public void reset() {
        set(new JavaDefault());
    }

    public void resetRhino() {
        set(new RhinoDefault());
    }

    public String remapClass(Class<?> from) {
        return get().remapClass(from);
    }

    public String unmapClass(String from) {
        return get().unmapClass(from);
    }

    public String remapField(Class<?> from, Field field) {
        return get().remapField(from, field);
    }

    public String remapMethod(Class<?> from, Method method) {
        return get().remapMethod(from, method);
    }
}
