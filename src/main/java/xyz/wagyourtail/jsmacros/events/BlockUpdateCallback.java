package xyz.wagyourtail.jsmacros.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import xyz.wagyourtail.jsmacros.reflector.BlockDataHelper;

public interface BlockUpdateCallback {
    Event<BlockUpdateCallback> EVENT = EventFactory.createArrayBacked(BlockUpdateCallback.class,
            (listeners) -> (b) -> {
                for (BlockUpdateCallback event : listeners) {
                    event.interact(b);
                }
            });
    void interact(BlockDataHelper b);
}
