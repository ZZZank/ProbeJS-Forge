package com.probejs.plugin;

import net.minecraftforge.eventbus.api.Event;

public class ForgeEventListener {

    public static void onEvent(Event event) {
        CapturedClasses.capturedRawEvents.put(event.getClass().getName(), event.getClass());
    }
}
