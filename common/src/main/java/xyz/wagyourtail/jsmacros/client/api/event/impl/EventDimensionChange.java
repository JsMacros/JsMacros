package xyz.wagyourtail.jsmacros.client.api.event.impl;

import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;

/**
 * @author Wagyourtail
 * @since 1.2.7
 */
 @Event(value = "DimensionChange", oldName = "DIMENSION_CHANGE")
public class EventDimensionChange implements BaseEvent {
    public final String dimension;
    
    public EventDimensionChange(String dimension) {
        this.dimension = dimension;
        profile.triggerEvent(this);
    }
    
    public String toString() {
        return String.format("%s:{\"dimension\": \"%s\"}", this.getEventName(), dimension);
    }

}
