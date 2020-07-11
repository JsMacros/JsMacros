package xyz.wagyourtail.jsmacros.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import xyz.wagyourtail.jsmacros.reflector.TextHelper;

public interface TitleCallback {
    Event<TitleCallback> EVENT = EventFactory.createArrayBacked(TitleCallback.class,
            (listeners) -> (type, message) -> {
                for (TitleCallback event : listeners) {
                    event.interact(type, message);
                }
            });
    void interact(String type, TextHelper textHelper);
}
