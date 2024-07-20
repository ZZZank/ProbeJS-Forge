package zzzank.probejs.mixins;

import dev.latvian.kubejs.script.ScriptManager;
import dev.latvian.mods.rhino.NativeJavaClass;
import dev.latvian.mods.rhino.Scriptable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import zzzank.probejs.lang.java.ClassRegistry;

import java.util.Collections;

@Mixin(ScriptManager.class)
public class OnJavaMixin {
    @Inject(method = "loadJavaClass", at = @At("RETURN"), remap = false)
    public void loadJavaClass(Scriptable scope, Object[] args, CallbackInfoReturnable<NativeJavaClass> cir) {
        var result = cir.getReturnValue();
        if (result == null) {
            return;
        }
        ClassRegistry.REGISTRY.fromClasses(Collections.singleton(result.getClassObject()));
    }
}
