package moe.wolfgirl.probejs.mixins;

import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.script.ScriptType;
import lombok.val;
import moe.wolfgirl.probejs.ProbeJS;
import moe.wolfgirl.probejs.docs.events.KubeEvents;
import moe.wolfgirl.probejs.features.kubejs.EventJSInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author ZZZank
 */
@Mixin(EventJS.class)
public class OnEventMixin {

    @Inject(
        method = "post(Ldev/latvian/kubejs/script/ScriptType;Ljava/lang/String;)Z",
        at = @At("HEAD"),
        remap = false
    )
    private void captureKjsEvents(ScriptType t, String id, CallbackInfoReturnable<Boolean> returns) {
        if (!ProbeJS.CONFIG.enabled.get()) {
            return;
        }
        val e = KubeEvents.KNOWN.get(id);
        if (e == null) {
            KubeEvents.KNOWN.put(id, new EventJSInfo(t, (EventJS) (Object) this, id, null));
        } else {
            e.scriptTypes().add(t);
        }
    }

    @Inject(
        method = "post(Ldev/latvian/kubejs/script/ScriptType;Ljava/lang/String;Ljava/lang/String;)Z",
        at = @At("HEAD"),
        remap = false
    )
    private void captureKjsSubEvents(ScriptType t, String id, String sub, CallbackInfoReturnable<Boolean> returns) {
        if (!ProbeJS.CONFIG.enabled.get()) {
            return;
        }
        val e = KubeEvents.KNOWN.get(id);
        if (e == null) {
            KubeEvents.KNOWN.put(id, new EventJSInfo(t, (EventJS) (Object) this, id, sub));
        } else {
            e.scriptTypes().add(t);
            if (e.sub().getValue() == null) {
                e.sub().setValue(sub);
            }
        }
    }
}

