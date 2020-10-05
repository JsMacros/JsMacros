package xyz.wagyourtail.jsmacros.api.events;

import xyz.wagyourtail.jsmacros.api.sharedinterfaces.IEvent;

/**
 * @author Wagyourtail
 * @since 1.2.7
 */
public class EventSendMessage implements IEvent {
    public String message;
    
    public EventSendMessage(String message) {
        this.message = message;
        profile.triggerMacroJoin(this);
    }

    public String toString() {
        return String.format("%s:{\"message\": \"%s\"}", this.getEventName(), message);
    }
}
