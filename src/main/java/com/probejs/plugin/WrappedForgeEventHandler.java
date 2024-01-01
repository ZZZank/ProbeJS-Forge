package com.probejs.plugin;

import dev.latvian.kubejs.forge.KubeJSForgeEventHandlerWrapper;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.NotNull;

public final class WrappedForgeEventHandler implements KubeJSForgeEventHandlerWrapper {

    private final String eventName;
    private final KubeJSForgeEventHandlerWrapper inner;

    public static Map<String, Class<?>> capturedEvents = new HashMap<>();

    public WrappedForgeEventHandler(String event, KubeJSForgeEventHandlerWrapper inner) {
        this.eventName = event;
        this.inner = inner;
    }

    @Override
    public void accept(Event event) {
        capturedEvents.put(eventName, event.getClass());
        inner.accept(event);
    }

    @NotNull
    @Override
    public Consumer<Event> andThen(@NotNull Consumer<? super Event> after) {
        return inner.andThen(after);
    }

    public String event() {
        return eventName;
    }

    public KubeJSForgeEventHandlerWrapper inner() {
        return inner;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        WrappedForgeEventHandler that = (WrappedForgeEventHandler) obj;
        return Objects.equals(this.eventName, that.eventName) && Objects.equals(this.inner, that.inner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventName, inner);
    }

    @Override
    public String toString() {
        return "WrappedEventHandler[event=" + eventName + ", inner=" + inner + ']';
    }
}
