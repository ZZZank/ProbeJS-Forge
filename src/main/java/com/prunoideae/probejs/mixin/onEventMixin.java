package com.prunoideae.probejs.mixin;

import com.prunoideae.probejs.plugin.WrappedEventHandler;
import dev.latvian.kubejs.BuiltinKubeJSPlugin;
import dev.latvian.kubejs.event.IEventHandler;
import dev.latvian.kubejs.script.BindingsEvent;
import dev.latvian.kubejs.util.ListJS;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(BuiltinKubeJSPlugin.class)
public class onEventMixin {

    @Overwrite(remap = false)
    private static Object onEvent(BindingsEvent event, Object[] args) {
        for (Object o : ListJS.orSelf(args[0])) {
            String e = String.valueOf(o);
            event.type.manager.get().events.listen(e, new WrappedEventHandler(e, (IEventHandler) args[1]));

        }
        return null;
    }
}