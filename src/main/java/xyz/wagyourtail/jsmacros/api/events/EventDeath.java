package xyz.wagyourtail.jsmacros.api.events;

import xyz.wagyourtail.jsmacros.api.sharedinterfaces.IEvent;

/**
 * @author Wagyourtail
 * @since 1.2.7
 */
public class EventDeath implements IEvent {

    
    public EventDeath() {
        
        profile.triggerMacro(this);
    }
    
    public String toString() {
        return String.format("%s:{}", this.getEventName());
    }
}
