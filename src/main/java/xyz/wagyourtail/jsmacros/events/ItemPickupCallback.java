package xyz.wagyourtail.jsmacros.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import xyz.wagyourtail.jsmacros.api.helpers.ItemStackHelper;

public interface ItemPickupCallback {
    Event<ItemPickupCallback> EVENT = EventFactory.createArrayBacked(ItemPickupCallback.class,
            (listeners) -> (b) -> {
                for (ItemPickupCallback event : listeners) {
                    event.interact(b);
                }
            });
    void interact(ItemStackHelper item);
}
