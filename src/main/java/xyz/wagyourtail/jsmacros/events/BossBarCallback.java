package xyz.wagyourtail.jsmacros.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import xyz.wagyourtail.jsmacros.reflector.TextHelper;

public interface BossBarCallback {
    Event<BossBarCallback> EVENT = EventFactory.createArrayBacked(BossBarCallback.class,
            (listeners) -> (type, uuid, style, color, name, percent) -> {
                for (BossBarCallback event : listeners) {
                    event.interact(type, uuid, style, color, name, percent);
                }
            });
    void interact(String type, String uuid, String style, String color, TextHelper name, float percent);
}
