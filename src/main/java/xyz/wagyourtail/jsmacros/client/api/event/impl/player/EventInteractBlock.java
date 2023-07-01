package xyz.wagyourtail.jsmacros.client.api.event.impl.player;

import xyz.wagyourtail.jsmacros.client.api.helpers.world.BlockDataHelper;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;

/**
 * @author Wagyourtail
 * @since 1.8.0
 */
@Event("InteractBlock")
public class EventInteractBlock implements BaseEvent {
    public final boolean offhand;
    public final String result;
    public final BlockDataHelper block;
    public final int side;

    public EventInteractBlock(boolean offhand, String resultStatus, BlockDataHelper block, int side) {
        this.offhand = offhand;
        this.result = resultStatus;
        this.block = block;
        this.side = side;

        profile.triggerEvent(this);
    }

    @Override
    public String toString() {
        return String.format("%s:{\"block\": %s, \"result\": \"%s\"}", this.getEventName(), block, result);
    }

}
