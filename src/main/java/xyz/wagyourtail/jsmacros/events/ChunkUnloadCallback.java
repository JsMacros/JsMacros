package xyz.wagyourtail.jsmacros.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface ChunkUnloadCallback {
    Event<ChunkUnloadCallback> EVENT = EventFactory.createArrayBacked(ChunkUnloadCallback.class,
            (listeners) -> (x, z) -> {
                for (ChunkUnloadCallback event : listeners) {
                    event.interact(x, z);
                }
            });
    void interact(int x, int z);
}
