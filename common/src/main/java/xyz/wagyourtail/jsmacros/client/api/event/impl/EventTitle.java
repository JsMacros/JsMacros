package xyz.wagyourtail.jsmacros.client.api.event.impl;

import net.minecraft.text.Text;
import xyz.wagyourtail.jsmacros.client.api.helpers.TextHelper;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;

/**
 * @author Wagyourtail
 * @since 1.2.7
 */
 @Event(value = "Title", oldName = "TITLE")
public class EventTitle implements BaseEvent {
    public final String type;
    public TextHelper message;
    
    public EventTitle(String type, Text message) {
        this.type = type;
        this.message = new TextHelper(message);
        
        profile.triggerEventJoinNoAnything(this);
    }
    
    public String toString() {
        return String.format("%s:{\"type\": \"%s\", \"message\": %s}", this.getEventName(), type, message.toString());
    }
}
