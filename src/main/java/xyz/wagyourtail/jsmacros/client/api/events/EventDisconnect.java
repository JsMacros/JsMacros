package xyz.wagyourtail.jsmacros.client.api.events;

import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;

/**
 * @author Wagyourtail
 * @since 1.2.7
 */
 @Event(value = "Disconnect", oldName = "DISCONNECT")
public class EventDisconnect implements BaseEvent {
    
    public EventDisconnect() {
        profile.triggerEvent(this);
    }

    public String toString() {
        return String.format("%s:{}", this.getEventName());
    }
}
