package xyz.wagyourtail.jsmacros.client.api.event.impl;

import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;

/**
 * @author Wagyourtail
 * @since 1.2.7
 */
 @Event(value = "EXPChange", oldName = "EXP_CHANGE")
public class EventEXPChange implements BaseEvent {
    public final float progress;
    public final int total;
    public final int level;
    
    public EventEXPChange(float progress, int total, int level) {
        this.progress = progress;
        this.total = total;
        this.level = level;
        
        profile.triggerEvent(this);
    }
    
    public String toString() {
        return String.format("%s:{\"total\": %d}", this.getEventName(), total);
    }
}
