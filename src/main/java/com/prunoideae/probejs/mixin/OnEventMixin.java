package com.prunoideae.probejs.mixin;

import com.prunoideae.probejs.plugin.WrappedEventHandler;
import dev.latvian.kubejs.event.EventsJS;
import dev.latvian.kubejs.event.IEventHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(EventsJS.class)
public class OnEventMixin {

    @ModifyVariable(method = "listen", argsOnly = true, at = @At("HEAD"), remap = false)
    private IEventHandler listen(IEventHandler handler, String id) {
        if (handler instanceof WrappedEventHandler) {
            return handler;
        }
        return new WrappedEventHandler(id, handler);
    }
}
