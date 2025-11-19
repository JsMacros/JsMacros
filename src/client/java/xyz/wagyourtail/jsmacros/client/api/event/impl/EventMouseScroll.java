package xyz.wagyourtail.jsmacros.client.api.event.impl;

import xyz.wagyourtail.jsmacros.client.JsMacrosClient;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;

/**
 * @author aMelonRind
 * @since 1.9.0
 */
@Event(value = "MouseScroll", cancellable = true)
public class EventMouseScroll extends BaseEvent {
    public final double deltaX;
    public final double deltaY;

    public EventMouseScroll(double deltaX, double deltaY) {
        super(JsMacrosClient.clientCore);
        this.deltaX = deltaX;
        this.deltaY = deltaY;
    }

    @Override
    public String toString() {
        return String.format("%s:{\"deltaX\": %s, \"deltaY\": %s}", this.getEventName(), deltaX, deltaY);
    }

}
