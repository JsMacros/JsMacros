package xyz.wagyourtail.jsmacros.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface DeathCallback {
    Event<DeathCallback> EVENT = EventFactory.createArrayBacked(DeathCallback.class,
            (listeners) -> () -> {
                for (DeathCallback event : listeners) {
                    event.interact();
                }
            });
    void interact();
}
