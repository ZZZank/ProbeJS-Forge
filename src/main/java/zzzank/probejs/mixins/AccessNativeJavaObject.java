package zzzank.probejs.mixins;

import dev.latvian.mods.rhino.NativeJavaObject;
import dev.latvian.mods.rhino.util.wrap.TypeWrappers;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * @author ZZZank
 */
@Mixin(value = NativeJavaObject.class, remap = false)
public interface AccessNativeJavaObject {
    @Invoker("coerceTypeImpl")
    static Object coerceTypeImpl(@Nullable TypeWrappers typeWrappers, Class<?> type, Object value) {
        return null;
    }
}
