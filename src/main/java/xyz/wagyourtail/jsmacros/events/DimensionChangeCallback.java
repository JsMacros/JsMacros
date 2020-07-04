package xyz.wagyourtail.jsmacros.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface DimensionChangeCallback {
    Event<DimensionChangeCallback> EVENT = EventFactory.createArrayBacked(DimensionChangeCallback.class,
            (listeners) -> (dimension) -> {
                for (DimensionChangeCallback event : listeners) {
                    event.interact(dimension);
                }
            });
    void interact(String dimension);
}
