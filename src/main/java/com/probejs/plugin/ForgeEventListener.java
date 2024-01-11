package com.probejs.plugin;

import java.util.HashMap;
import java.util.Map;

import net.minecraftforge.eventbus.api.Event;

public class ForgeEventListener {

    public static Map<String, Class<? extends Event>> capturedEvents = new HashMap<>();

    public static void onEvent(Event event) {
        capturedEvents.put(event.getClass().getName(), event.getClass());
    }
}
