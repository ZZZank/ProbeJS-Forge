package zzzank.probejs.mixins;

import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.script.ScriptType;
import lombok.val;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import zzzank.probejs.ProbeJS;
import zzzank.probejs.features.kubejs.EventJSInfo;
import zzzank.probejs.features.kubejs.EventJSInfos;

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
        val e = EventJSInfos.KNOWN.get(id);
        if (e == null) {
            EventJSInfos.KNOWN.put(id, new EventJSInfo(t, (EventJS) (Object) this, id, null));
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
        val e = EventJSInfos.KNOWN.get(id);
        if (e == null) {
            EventJSInfos.KNOWN.put(id, new EventJSInfo(t, (EventJS) (Object) this, id, sub));
        } else {
            e.scriptTypes().add(t);
            if (e.sub().getValue() == null) {
                e.sub().setValue(sub);
            }
        }
    }
}

