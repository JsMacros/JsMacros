package xyz.wagyourtail.jsmacros.events;

import java.util.UUID;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface PlayerJoinCallback {
    Event<PlayerJoinCallback> EVENT = EventFactory.createArrayBacked(PlayerJoinCallback.class,
            (listeners) -> (uuid, pName) -> {
                for (PlayerJoinCallback event : listeners) {
                    event.interact(uuid, pName);
                }
            });
    void interact(UUID uuid, String pName);
}
