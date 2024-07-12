package moe.wolfgirl.probejs.mixins.integration;

import dev.latvian.kubejs.forge.BuiltinKubeJSForgePlugin;
import dev.latvian.mods.rhino.NativeJavaClass;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * @author ZZZank
 */
@Mixin(BuiltinKubeJSForgePlugin.class)
public abstract class KubeJSForgeEventRedirection {

    @ModifyVariable(method = "onPlatformEvent", at = @At("HEAD"), index = 1, argsOnly = true, remap = false)
    private static Object[] pjs$forgeEventRedirecting(Object[] args) {
        if (args.length > 0 && args[0] instanceof NativeJavaClass clazz) {
            args[0] = clazz.getClassObject().getName();
        }
        return args;
    }
}
