package zzzank.probejs.features.forge_scan;

import lombok.val;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.objectweb.asm.Type;
import zzzank.probejs.ProbeJS;
import zzzank.probejs.utils.GameUtils;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.util.Set;

/**
 * @author ZZZank
 */
public class AccessClassData {

    public static final MethodHandle accessClazz;
    public static final MethodHandle accessParent;
    public static final MethodHandle accessInterfaces;

    static {
        val lookup = MethodHandles.lookup();
        val c = ModFileScanData.ClassData.class;
        try {
            var f = c.getDeclaredField("clazz");
            f.setAccessible(true);
            accessClazz = lookup.unreflectGetter(f);
            f = c.getDeclaredField("parent");
            f.setAccessible(true);
            accessParent = lookup.unreflectGetter(f);
            f = c.getDeclaredField("interfaces");
            f.setAccessible(true);
            accessInterfaces = lookup.unreflectGetter(f);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            ProbeJS.LOGGER.error("accessing '{}' failed", ModFileScanData.ClassData.class);
            GameUtils.logThrowable(e);
            throw new IllegalStateException();
        }
    }

    private final ModFileScanData.ClassData raw;

    public AccessClassData(ModFileScanData.ClassData raw) {
        this.raw = raw;
    }

    //    @SneakyThrows
    public Type pjs$clazz() {
        try {
            return (Type) accessClazz.invoke(raw);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public Type pjs$parent() {
        try {
            return (Type) accessParent.invoke(raw);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public Set<Type> pjs$interfaces() {
        try {
            return (Set<Type>) accessInterfaces.invoke(raw);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
