package xyz.wagyourtail.jsmacros.client.api.event.impl;

import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;

/**
 * @author Wagyourtail
 * @since 1.2.7
 */
@Event(value = "SendMessage", oldName = "SEND_MESSAGE", cancellable = true)
public class EventSendMessage extends BaseEvent {
    public String message;

    public EventSendMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return String.format("%s:{\"message\": \"%s\"}", this.getEventName(), message);
    }

}
