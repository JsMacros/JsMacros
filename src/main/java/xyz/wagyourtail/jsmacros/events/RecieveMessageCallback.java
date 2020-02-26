package xyz.wagyourtail.jsmacros.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface RecieveMessageCallback {
    Event<RecieveMessageCallback> EVENT = EventFactory.createArrayBacked(RecieveMessageCallback.class,
            (listeners) -> (message) -> {
                for (RecieveMessageCallback event : listeners) {
                    message = event.interact(message);
                }
                return message;
            });
    String interact(String message);
}
