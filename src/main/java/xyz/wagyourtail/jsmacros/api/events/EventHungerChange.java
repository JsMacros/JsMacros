package xyz.wagyourtail.jsmacros.api.events;

import xyz.wagyourtail.jsmacros.api.sharedinterfaces.IEvent;

/**
 * @author Wagyourtail
 * @since 1.2.7
 */
public class EventHungerChange implements IEvent {
    public final int foodLevel;
    
    public EventHungerChange(int foodLevel) {
        this.foodLevel = foodLevel;
        
        profile.triggerMacro(this);
    }

    public String toString() {
        return String.format("%s:{\"foodLevel\": %d}", this.getEventName(), foodLevel);
    }
}
