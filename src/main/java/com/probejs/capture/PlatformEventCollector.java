package com.probejs.capture;

import com.probejs.ProbeJS;
import com.probejs.capture.mixin.EventListenerHelperMixin;
import lombok.val;

/**
 * @author ZZZank
 */
public abstract class PlatformEventCollector {

    public static void init() {
        if (!ProbeJS.ENABLED) {
            return;
        }
        val listeners = EventListenerHelperMixin.getListeners();
        val lock = EventListenerHelperMixin.getLock();
        lock.readLock().lock();
        for (Class<?> clazz : listeners.keySet()) {
            CapturedClasses.capturedRawEvents.put(clazz.getName(), clazz);
        }
        lock.readLock().unlock();
    }
}
