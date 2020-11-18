package xyz.wagyourtail.jsmacros.api.events;

import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;

/**
 * @author Wagyourtail
 * @since 1.2.7
 */
 @Event(value = "SendMessage", oldName = "SEND_MESSAGE")
public class EventSendMessage implements BaseEvent {
    public String message;
    
    public EventSendMessage(String message) {
        this.message = message;
        profile.triggerMacroJoin(this);
    }

    public String toString() {
        return String.format("%s:{\"message\": \"%s\"}", this.getEventName(), message);
    }
}
