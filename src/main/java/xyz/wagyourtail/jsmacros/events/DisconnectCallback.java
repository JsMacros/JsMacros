package xyz.wagyourtail.jsmacros.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface DisconnectCallback {
    Event<DisconnectCallback> EVENT = EventFactory.createArrayBacked(DisconnectCallback.class,
            (listeners) -> () -> {
                for (DisconnectCallback event : listeners) {
                    event.interact();
                }
            });
    void interact();
}
