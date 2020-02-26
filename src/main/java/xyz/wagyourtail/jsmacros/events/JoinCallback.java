package xyz.wagyourtail.jsmacros.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.network.ServerPlayerEntity;

public interface JoinCallback {
    Event<JoinCallback> EVENT = EventFactory.createArrayBacked(JoinCallback.class, 
        (listeners) -> (conn, player) -> {
            for (JoinCallback event : listeners) {
                event.interact(conn, player);
            }
    });
    
    void interact(ClientConnection conn, ServerPlayerEntity player);
}
