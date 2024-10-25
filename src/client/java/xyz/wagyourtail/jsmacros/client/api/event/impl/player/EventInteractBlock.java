package xyz.wagyourtail.jsmacros.client.api.event.impl.player;

import xyz.wagyourtail.doclet.DocletReplaceReturn;
import xyz.wagyourtail.jsmacros.client.api.helper.world.BlockDataHelper;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;

/**
 * @author Wagyourtail
 * @since 1.8.0
 */
@Event("InteractBlock")
public class EventInteractBlock extends BaseEvent {
    public final boolean offhand;
    @DocletReplaceReturn("ActionResult")
    public final String result;
    public final BlockDataHelper block;
    @DocletReplaceReturn("Side")
    public final int side;

    public EventInteractBlock(boolean offhand, String resultStatus, BlockDataHelper block, int side) {
        this.offhand = offhand;
        this.result = resultStatus;
        this.block = block;
        this.side = side;
    }

    @Override
    public String toString() {
        return String.format("%s:{\"block\": %s, \"result\": \"%s\"}", this.getEventName(), block, result);
    }

}
