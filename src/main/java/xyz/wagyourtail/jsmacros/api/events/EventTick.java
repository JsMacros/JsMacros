package xyz.wagyourtail.jsmacros.api.events;

import xyz.wagyourtail.jsmacros.api.sharedinterfaces.IEvent;

/**
 * @author Wagyourtail
 * @since 1.2.7
 */
public class EventTick implements IEvent {

    public EventTick() {
        profile.triggerMacroNoAnything(this);
    }

    public String toString() {
        return String.format("%s:{}", this.getEventName());
    }
}
