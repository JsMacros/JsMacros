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
    /**
     * @since 1.6.5
     */
    public final float prevProgress;
    /**
     * @since 1.6.5
     */
    public final int prevTotal;
    /**
     * @since 1.6.5
     */
    public final int prevLevel;

    public EventEXPChange(float progress, int total, int level, float prevProgress, int prevTotal, int prevLevel) {
        this.progress = progress;
        this.total = total;
        this.level = level;

        this.prevProgress = prevProgress;
        this.prevTotal = prevTotal;
        this.prevLevel = prevLevel;
        
        profile.triggerEvent(this);
    }
    
    public String toString() {
        return String.format("%s:{\"total\": %d}", this.getEventName(), total);
    }
}
