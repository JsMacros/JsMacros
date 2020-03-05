package xyz.wagyourtail.jsmacros.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface AirChangeCallback {
    Event<AirChangeCallback> EVENT = EventFactory.createArrayBacked(AirChangeCallback.class,
            (listeners) -> (air) -> {
                for (AirChangeCallback event : listeners) {
                    event.interact(air);
                }
            });
    void interact(int air);
}
