package xyz.wagyourtail.jsmacros.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import xyz.wagyourtail.jsmacros.reflector.TextHelper;

public interface RecieveMessageCallback {
    Event<RecieveMessageCallback> EVENT = EventFactory.createArrayBacked(RecieveMessageCallback.class,
            (listeners) -> (message) -> {
                for (RecieveMessageCallback event : listeners) {
                    message = event.interact(message);
                }
                return message;
            });
    TextHelper interact(TextHelper message);
}
