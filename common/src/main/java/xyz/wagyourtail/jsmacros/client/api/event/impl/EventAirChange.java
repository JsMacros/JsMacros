package xyz.wagyourtail.jsmacros.client.api.event.impl;

import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;

/**
 * @author Wagyourtail
 * @since 1.2.7
 */
 @Event(value = "AirChange", oldName = "AIR_CHANGE")
public class EventAirChange implements BaseEvent {
    public final int air;
    
    public EventAirChange(int air) {
        this.air = air;
        
        profile.triggerEvent(this);
    }
    
    public String toString() {
        return String.format("%s:{\"air\": %d}", this.getEventName(), air);
    }
}
