package xyz.wagyourtail.jsmacros.client.api.event.impl.world;

import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;

/**
 * @author Wagyourtail
 * @since 1.2.7
 */
@Event(value = "ChunkLoad", oldName = "CHUNK_LOAD")
public class EventChunkLoad implements BaseEvent {
    public final int x;
    public final int z;
    public final boolean isFull;

    public EventChunkLoad(int x, int z, boolean isFull) {
        this.x = x;
        this.z = z;
        this.isFull = isFull;

        profile.triggerEvent(this);
    }

    @Override
    public String toString() {
        return String.format("%s:{\"x\": %d, \"z\": %d}", this.getEventName(), x, z);
    }

}
