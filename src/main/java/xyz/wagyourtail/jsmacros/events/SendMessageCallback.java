package xyz.wagyourtail.jsmacros.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface SendMessageCallback {
    Event<SendMessageCallback> EVENT = EventFactory.createArrayBacked(SendMessageCallback.class,
            (listeners) -> (message) -> {
                for (SendMessageCallback event : listeners) {
                    message = event.interact(message);
                }
                return message;
            });
    String interact(String message);
}
