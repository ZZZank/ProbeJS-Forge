package zzzank.probejs.mixins.patch;

import dev.latvian.mods.rhino.NativeJavaObject;
import dev.latvian.mods.rhino.util.wrap.TypeWrappers;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * @author ZZZank
 */
@Mixin(value = NativeJavaObject.class, remap = false)
public abstract class MixinNativeJavaObject {
    @Invoker("coerceTypeImpl")
    public static Object coerceTypeImpl(@Nullable TypeWrappers typeWrappers, Class<?> type, Object value) {
        return null;
    }
}
