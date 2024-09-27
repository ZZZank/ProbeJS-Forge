package zzzank.probejs.features.forge_scan;

import lombok.SneakyThrows;
import lombok.val;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.objectweb.asm.Type;

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
            accessClazz = lookup.findGetter(c, "clazz", Type.class);
            accessParent = lookup.findGetter(c, "parent", Type.class);
            accessInterfaces = lookup.findGetter(c, "interfaces", Set.class);
        } catch (NoSuchFieldException | IllegalAccessException ignored) {
            throw new IllegalStateException();
        }
    }

    private final ModFileScanData.ClassData raw;

    public AccessClassData(ModFileScanData.ClassData raw) {
        this.raw = raw;
    }

    @SneakyThrows
    public Type pjs$clazz() {
        return (Type) accessClazz.invoke(raw);
    }
    @SneakyThrows
    public Type pjs$parent() {
        return (Type) accessParent.invoke(raw);
    }
    @SneakyThrows
    public Set<Type> pjs$interfaces() {
        return (Set<Type>) accessInterfaces.invoke(raw);
    }
}
