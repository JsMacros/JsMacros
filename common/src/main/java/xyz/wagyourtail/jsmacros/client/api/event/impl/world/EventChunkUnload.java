package xyz.wagyourtail.jsmacros.client.api.event.impl.world;

import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;

/**
 * @author Wagyourtail
 * @since 1.2.7
 */
 @Event(value = "ChunkUnload", oldName = "CHUNK_UNLOAD")
public class EventChunkUnload implements BaseEvent {
    public final int x;
    public final int z;
    
    public EventChunkUnload(int x, int z) {
        this.x = x;
        this.z = z;
        
        profile.triggerEvent(this);
    }
    
    @Override
    public String toString() {
        return String.format("%s:{\"x\": %d, \"z\": %d}", this.getEventName(), x, z);
    }
}
