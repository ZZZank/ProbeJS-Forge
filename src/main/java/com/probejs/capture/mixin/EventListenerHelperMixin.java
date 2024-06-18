package com.probejs.capture.mixin;

import net.minecraftforge.eventbus.ListenerList;
import net.minecraftforge.eventbus.api.EventListenerHelper;
import org.jetbrains.annotations.Contract;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * @author ZZZank
 */
@Mixin(EventListenerHelper.class)
public interface EventListenerHelperMixin {

    @Accessor("listeners")
    @Contract(" -> _")
    static Map<Class<?>, ListenerList> getListeners() {
        throw new AssertionError();
    }

    @Accessor("lock")
    @Contract(" -> _")
    static ReadWriteLock getLock() {
        throw new AssertionError();
    }
}
