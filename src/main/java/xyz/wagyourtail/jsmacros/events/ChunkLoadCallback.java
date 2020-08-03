package xyz.wagyourtail.jsmacros.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface ChunkLoadCallback {
    Event<ChunkLoadCallback> EVENT = EventFactory.createArrayBacked(ChunkLoadCallback.class,
            (listeners) -> (x, z) -> {
                for (ChunkLoadCallback event : listeners) {
                    event.interact(x, z);
                }
            });
    void interact(int x, int z);
}
