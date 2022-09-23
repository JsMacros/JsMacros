package xyz.wagyourtail.jsmacros.client.api.event.impl;

import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;

@Event("JoinedTick")
public class EventJoinedTick implements BaseEvent {

    public EventJoinedTick() {
        profile.triggerEventJoinNoAnything(this);
    }

    @Override
    public String toString() {
        return String.format("%s:{}", this.getEventName());
    }
}
