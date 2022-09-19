package xyz.wagyourtail.jsmacros.client.api.event.impl;

import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;

/**
 * @author Etheradon
 * @since 1.9.0
 */
@Event(value = "LaunchGame")
public class EventLaunchGame implements BaseEvent {
    
    public final String playerName;

    public EventLaunchGame(String playerName) {
        this.playerName = playerName;
        profile.triggerEvent(this);
    }

    @Override
    public String toString() {
        return String.format("%s:{\"name\": \"%s\"}", this.getEventName(), playerName);
    }
}
