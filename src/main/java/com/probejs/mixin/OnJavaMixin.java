package com.probejs.mixin;

import com.probejs.capture.CapturedClasses;
import dev.latvian.kubejs.script.ScriptManager;
import dev.latvian.mods.rhino.NativeJavaClass;
import dev.latvian.mods.rhino.Scriptable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ScriptManager.class)
public abstract class OnJavaMixin {

    @Inject(method = "loadJavaClass", at = @At("RETURN"), remap = false)
    public void captureJavaClass(Scriptable scope, Object[] args, CallbackInfoReturnable<NativeJavaClass> cir) {
        CapturedClasses.capturedJavaClasses.add(cir.getReturnValue().getClassObject());
    }
}
