package xyz.wagyourtail.jsmacros.client.api.event.impl.world;

import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;

/**
 * @author Wagyourtail
 * @since 1.2.7
 */
 @Event(value = "Tick", oldName = "TICK")
public class EventTick implements BaseEvent {

    public EventTick() {
        profile.triggerEventNoAnything(this);
    }

    @Override
    public String toString() {
        return String.format("%s:{}", this.getEventName());
    }
}
