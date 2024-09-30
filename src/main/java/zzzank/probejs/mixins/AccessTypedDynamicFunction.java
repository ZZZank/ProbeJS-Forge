package zzzank.probejs.mixins;

import dev.latvian.kubejs.script.TypedDynamicFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * @author ZZZank
 */
@Mixin(value = TypedDynamicFunction.class, remap = false)
public interface AccessTypedDynamicFunction {

    @Accessor("types")
    Class<?>[] types();
}
