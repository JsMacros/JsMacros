package xyz.wagyourtail.jsmacros.api.events;

import xyz.wagyourtail.jsmacros.api.sharedinterfaces.IEvent;

/**
 * @author Wagyourtail
 * @since 1.2.7
 */
public class EventAirChange implements IEvent {
    public final int air;
    
    public EventAirChange(int air) {
        this.air = air;
        
        profile.triggerMacro(this);
    }
    
    public String toString() {
        return String.format("%s:{\"air\": %d}", this.getEventName(), air);
    }
}
