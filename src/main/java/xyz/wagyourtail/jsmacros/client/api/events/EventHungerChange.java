package xyz.wagyourtail.jsmacros.client.api.events;

import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;

/**
 * @author Wagyourtail
 * @since 1.2.7
 */
 @Event(value = "HungerChange", oldName = "HUNGER_CHANGE")
public class EventHungerChange implements BaseEvent {
    public final int foodLevel;
    
    public EventHungerChange(int foodLevel) {
        this.foodLevel = foodLevel;
        
        profile.triggerEvent(this);
    }

    public String toString() {
        return String.format("%s:{\"foodLevel\": %d}", this.getEventName(), foodLevel);
    }
}
