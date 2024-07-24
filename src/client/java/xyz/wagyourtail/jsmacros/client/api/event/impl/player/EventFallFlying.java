package xyz.wagyourtail.jsmacros.client.api.event.impl.player;

import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;

@Event("FallFlying")
public class EventFallFlying extends BaseEvent {
    public final boolean state;

    public EventFallFlying(boolean state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return String.format("%s:{\"state\": %s}", this.getEventName(), state);
    }

}
