package xyz.wagyourtail.jsmacros.client.api.event.impl.player;

import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;

/**
 * @author Wagyourtail
 * @since 1.2.7
 */
@Event(value = "HungerChange", oldName = "HUNGER_CHANGE")
public class EventHungerChange extends BaseEvent {
    public final int foodLevel;

    public EventHungerChange(int foodLevel) {
        this.foodLevel = foodLevel;
    }

    @Override
    public String toString() {
        return String.format("%s:{\"foodLevel\": %d}", this.getEventName(), foodLevel);
    }

}
