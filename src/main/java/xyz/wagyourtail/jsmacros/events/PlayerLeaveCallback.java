package xyz.wagyourtail.jsmacros.events;

import java.util.UUID;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface PlayerLeaveCallback {
    Event<PlayerLeaveCallback> EVENT = EventFactory.createArrayBacked(PlayerLeaveCallback.class,
            (listeners) -> (uuid, pName) -> {
                for (PlayerLeaveCallback event : listeners) {
                    event.interact(uuid, pName);
                }
            });
    void interact(UUID uuid, String pName);
}
