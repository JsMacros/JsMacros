package xyz.wagyourtail.jsmacros.api.events;

import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;

/**
 * @author Wagyourtail
 * @since 1.2.7
 */
 @Event(value = "Death", oldName = "DEATH")
public class EventDeath implements BaseEvent {

    
    public EventDeath() {
        
        profile.triggerMacro(this);
    }
    
    public String toString() {
        return String.format("%s:{}", this.getEventName());
    }
}
