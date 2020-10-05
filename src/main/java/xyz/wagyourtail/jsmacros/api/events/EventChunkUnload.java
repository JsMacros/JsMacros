package xyz.wagyourtail.jsmacros.api.events;

import xyz.wagyourtail.jsmacros.api.sharedinterfaces.IEvent;

/**
 * @author Wagyourtail
 * @since 1.2.7
 */
public class EventChunkUnload implements IEvent {
    public final int x;
    public final int z;
    
    public EventChunkUnload(int x, int z) {
        this.x = x;
        this.z = z;
        
        profile.triggerMacro(this);
    }
    
    public String toString() {
        return String.format("%s:{\"x\": %d, \"z\": %d}", this.getEventName(), x, z);
    }
}
