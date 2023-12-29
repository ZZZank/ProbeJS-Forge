package com.prunoideae.probejs.plugin;

import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.event.IEventHandler;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class WrappedEventHandler implements IEventHandler {

    private final String event;
    private final IEventHandler handler;

    public static Map<String, Class<? extends EventJS>> capturedEvents = new HashMap<>();

    @Override
    public void onEvent(EventJS eventJS) {
        capturedEvents.put(this.event, eventJS.getClass());
        this.handler.onEvent(eventJS);
    }

    public WrappedEventHandler(String event, IEventHandler inner) {
        this.event = event;
        this.handler = inner;
    }

    public String event() {
        return event;
    }

    public IEventHandler inner() {
        return handler;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        WrappedEventHandler that = (WrappedEventHandler) obj;
        return Objects.equals(this.event, that.event) && Objects.equals(this.handler, that.handler);
    }

    @Override
    public int hashCode() {
        return Objects.hash(event, handler);
    }

    @Override
    public String toString() {
        return "WrappedEventHandler[" + "event=" + event + ", " + "inner=" + handler + ']';
    }
}
