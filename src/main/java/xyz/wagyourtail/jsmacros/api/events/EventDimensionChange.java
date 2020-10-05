package xyz.wagyourtail.jsmacros.api.events;

import xyz.wagyourtail.jsmacros.api.sharedinterfaces.IEvent;

/**
 * @author Wagyourtail
 * @since 1.2.7
 */
public class EventDimensionChange implements IEvent {
    public final String dimension;
    
    public EventDimensionChange(String dimension) {
        this.dimension = dimension;
        profile.triggerMacro(this);
    }
    
    public String toString() {
        return String.format("%s:{\"dimension\": \"%s\"}", this.getEventName(), dimension);
    }

}
