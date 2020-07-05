package xyz.wagyourtail.jsmacros.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface OpenScreenCallback {
    Event<OpenScreenCallback> EVENT = EventFactory.createArrayBacked(OpenScreenCallback.class,
            (listeners) -> (screenName) -> {
                for (OpenScreenCallback event : listeners) {
                    event.interact(screenName);
                }
            });
    void interact(String screenName);
}
