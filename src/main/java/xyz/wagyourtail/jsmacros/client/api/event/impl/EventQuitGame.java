package xyz.wagyourtail.jsmacros.client.api.event.impl;

import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@Event(value = "QuitGame")
public class EventQuitGame implements BaseEvent {

    public EventQuitGame() {
        profile.triggerEventJoinNoAnything(this);
    }

    @Override
    public String toString() {
        return String.format("%s:{}", this.getEventName());
    }

}
