package zzzank.probejs.mixins.access;

import dev.latvian.kubejs.script.TypedDynamicFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * @author ZZZank
 */
@Mixin(TypedDynamicFunction.class)
public interface TypedDynamicFunctionAccess {

    @Accessor("types")
    Class<?>[] types();
}
