package xyz.wagyourtail.jsmacros.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import xyz.wagyourtail.jsmacros.api.helpers.BlockDataHelper;

public interface BlockUpdateCallback {
    Event<BlockUpdateCallback> EVENT = EventFactory.createArrayBacked(BlockUpdateCallback.class,
            (listeners) -> (b, type) -> {
                for (BlockUpdateCallback event : listeners) {
                    event.interact(b, type);
                }
            });
    void interact(BlockDataHelper b, String type);
}
