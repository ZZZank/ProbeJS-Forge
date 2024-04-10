package com.probejs.mixin;

import com.probejs.ProbeJS;
import com.probejs.info.EventInfo;
import com.probejs.plugin.CapturedClasses;
import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.script.ScriptType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EventJS.class)
public class OnEventMixin {

    @Inject(
        method = "post(Ldev/latvian/kubejs/script/ScriptType;Ljava/lang/String;)Z",
        at = @At("HEAD"),
        remap = false
    )
    private void captureKjsEvents(ScriptType t, String id, CallbackInfoReturnable<Boolean> returns) {
        if (ProbeJS.ENABLED && !CapturedClasses.isEventIgnored(this.getClass())) {
            if (!CapturedClasses.capturedEvents.containsKey(id)) {
                CapturedClasses.capturedEvents.put(id, new EventInfo(t, (EventJS) (Object) this, id, null));
            } else {
                CapturedClasses.capturedEvents.get(id).scriptTypes.add(t);
            }
        }
    }

    @Inject(
        method = "post(Ldev/latvian/kubejs/script/ScriptType;Ljava/lang/String;Ljava/lang/String;)Z",
        at = @At("HEAD"),
        remap = false
    )
    private void captureKjsSubEvents(ScriptType t, String id, String sub, CallbackInfoReturnable<Boolean> returns) {
        if (ProbeJS.ENABLED && !CapturedClasses.isEventIgnored(this.getClass())) {
            if (!CapturedClasses.capturedEvents.containsKey(id)) {
                CapturedClasses.capturedEvents.put(
                    id + "." + sub,
                    new EventInfo(t, (EventJS) (Object) this, id, sub)
                );
            } else {
                CapturedClasses.capturedEvents.get(id).scriptTypes.add(t);
            }
        }
    }
}
