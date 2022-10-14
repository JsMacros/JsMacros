package xyz.wagyourtail.jsmacros.client.api.event.impl;

import net.minecraft.util.IChatComponent;
import xyz.wagyourtail.jsmacros.client.api.helpers.TextHelper;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;

/**
 * @author Wagyourtail
 * @since 1.2.7
 */
 @Event(value = "Disconnect", oldName = "DISCONNECT")
public class EventDisconnect implements BaseEvent {
    /**
     * @since 1.6.4
     */
    public final TextHelper message;
    
    public EventDisconnect(IChatComponent message) {
        this.message = new TextHelper(message);
        profile.triggerEvent(this);
    }

    public String toString() {
        return String.format("%s:{}", this.getEventName());
    }
}
