package xyz.wagyourtail.jsmacros.api.events;

import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;

/**
 * @author Wagyourtail
 * @since 1.2.7
 */
 @Event(value = "Tick", oldName = "TICK")
public class EventTick implements BaseEvent {

    public EventTick() {
        profile.triggerMacroNoAnything(this);
    }

    public String toString() {
        return String.format("%s:{}", this.getEventName());
    }
}
