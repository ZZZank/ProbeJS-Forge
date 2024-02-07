package com.probejs.plugin;

// import dev.latvian.kubejs.RegistryObjectBuilderTypes;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CapturedClasses {

    public static final Map<String, CapturedEvent> capturedEvents = new HashMap<>();
    public static final Map<String, Class<?>> capturedRawEvents = new HashMap<>();
    public static final Set<Class<?>> capturedJavaClasses = new HashSet<>();
    public static final Set<Class<?>> ignoredEvents = new HashSet<>();

    static {
        // ignoredEvents.add(null);
    }

    public static boolean isEventIgnored(Class<?> clazz) {
        if (ignoredEvents.isEmpty()) {
            return false;
        }
        return ignoredEvents.stream().anyMatch(ignored -> ignored.isAssignableFrom(clazz));
    }
}
