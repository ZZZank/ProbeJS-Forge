package zzzank.probejs.mixins;


import net.minecraftforge.eventbus.EventBus;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBusInvokeDispatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import zzzank.probejs.GlobalStates;

@Mixin(EventBus.class)
public abstract class MixinEventBus {

    /*
     * So we sneak peek all registered event listeners
     */
    @Inject(method = "post(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraftforge/eventbus/api/IEventBusInvokeDispatcher;)Z", at = @At("HEAD"), remap = false)
    public void addToListeners(Event event, IEventBusInvokeDispatcher wrapper, CallbackInfoReturnable<Boolean> cir) {
        GlobalStates.KNOWN_EVENTS.add(event.getClass());
    }
}
