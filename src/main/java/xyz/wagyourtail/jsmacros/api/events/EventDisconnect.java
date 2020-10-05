package xyz.wagyourtail.jsmacros.api.events;

import xyz.wagyourtail.jsmacros.api.sharedinterfaces.IEvent;

/**
 * @author Wagyourtail
 * @since 1.2.7
 */
public class EventDisconnect implements IEvent {
    
    public EventDisconnect() {
        profile.triggerMacro(this);
    }

    public String toString() {
        return String.format("%s:{}", this.getEventName());
    }
}
