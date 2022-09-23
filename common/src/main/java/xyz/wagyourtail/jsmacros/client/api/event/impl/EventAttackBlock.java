package xyz.wagyourtail.jsmacros.client.api.event.impl;

import xyz.wagyourtail.jsmacros.client.api.helpers.block.BlockDataHelper;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;

@Event("AttackBlock")
public class EventAttackBlock implements BaseEvent {
    public final BlockDataHelper block;
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
