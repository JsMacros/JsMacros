package xyz.wagyourtail.jsmacros.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import xyz.wagyourtail.jsmacros.reflector.ItemStackHelper;

public interface ArmorChangeCallback {
    Event<ArmorChangeCallback> EVENT = EventFactory.createArrayBacked(ArmorChangeCallback.class,
            (listeners) -> (slot, item) -> {
                for (ArmorChangeCallback event : listeners) {
                    event.interact(slot, item);
                }
            });
    void interact(String slot, ItemStackHelper item);
}
