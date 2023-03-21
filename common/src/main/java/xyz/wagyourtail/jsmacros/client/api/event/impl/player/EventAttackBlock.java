package xyz.wagyourtail.jsmacros.client.api.event.impl.player;

import xyz.wagyourtail.doclet.DocletReplaceReturn;
import xyz.wagyourtail.jsmacros.client.api.helpers.world.BlockDataHelper;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;

@Event("AttackBlock")
public class EventAttackBlock implements BaseEvent {
    public final BlockDataHelper block;
    @DocletReplaceReturn("Side")
    public final int side;

    public EventAttackBlock(BlockDataHelper block, int side) {
        this.block = block;
        this.side = side;

        profile.triggerEvent(this);
    }

    @Override
    public String toString() {
        return String.format("%s:{\"block\": %s}", this.getEventName(), block);
    }
    
}
