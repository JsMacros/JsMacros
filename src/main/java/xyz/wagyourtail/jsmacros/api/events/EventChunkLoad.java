package xyz.wagyourtail.jsmacros.api.events;

import xyz.wagyourtail.jsmacros.api.sharedinterfaces.IEvent;

/**
 * @author Wagyourtail
 * @since 1.2.7
 */
public class EventChunkLoad implements IEvent {
    public final int x;
    public final int z;
    public final boolean isFull;
    
    public EventChunkLoad(int x, int z, boolean isFull) {
        this.x = x;
        this.z = z;
        this.isFull = isFull;
        
        profile.triggerMacro(this);
    }
    
    public String toString() {
        return String.format("%s:{\"x\": %d, \"z\": %d}", this.getEventName(), x, z);
    }
}
