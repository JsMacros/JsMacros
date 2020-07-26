package xyz.wagyourtail.jsmacros.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import xyz.wagyourtail.jsmacros.reflector.ItemStackHelper;

public interface HeldItemCallback {
    Event<HeldItemCallback> EVENT = EventFactory.createArrayBacked(HeldItemCallback.class,
            (listeners) -> (slot, offhand) -> {
                for (HeldItemCallback event : listeners) {
                    event.interact(slot, offhand);
                }
            });
    void interact(ItemStackHelper slot, boolean offhand);
}
