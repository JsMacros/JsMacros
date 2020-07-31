package xyz.wagyourtail.jsmacros.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import xyz.wagyourtail.jsmacros.reflector.ItemStackHelper;

public interface ArmorChangeCallback {
    Event<ArmorChangeCallback> EVENT = EventFactory.createArrayBacked(ArmorChangeCallback.class,
            (listeners) -> (slot, item, oldItem) -> {
                for (ArmorChangeCallback event : listeners) {
                    event.interact(slot, item, oldItem);
                }
            });
    void interact(String slot, ItemStackHelper item, ItemStackHelper oldItem);
}
