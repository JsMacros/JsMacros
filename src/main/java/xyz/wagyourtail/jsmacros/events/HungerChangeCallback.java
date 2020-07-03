package xyz.wagyourtail.jsmacros.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface HungerChangeCallback {
    Event<HungerChangeCallback> EVENT = EventFactory.createArrayBacked(HungerChangeCallback.class,
            (listeners) -> (foodLevel) -> {
                for (HungerChangeCallback event : listeners) {
                    event.interact(foodLevel);
                }
            });
    void interact(int foodLevel);
}
