package zzzank.probejs.mixins;

import net.minecraftforge.forgespi.language.ModFileScanData;
import org.objectweb.asm.Type;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

/**
 * @author ZZZank
 */
@Mixin(value = ModFileScanData.ClassData.class, remap = false)
public interface AccessClassData {

    @Accessor("clazz")
    Type pjs$clazz();

    @Accessor("parent")
    Type pjs$parent();

    @Accessor("interfaces")
    Set<Type> pjs$interfaces();
}
