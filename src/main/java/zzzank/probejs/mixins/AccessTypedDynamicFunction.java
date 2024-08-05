package zzzank.probejs.mixins;

import dev.latvian.kubejs.script.TypedDynamicFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * @author ZZZank
 */
@Mixin(TypedDynamicFunction.class)
public interface AccessTypedDynamicFunction {

    @Accessor(value = "types", remap = false)
    Class<?>[] types();
}
