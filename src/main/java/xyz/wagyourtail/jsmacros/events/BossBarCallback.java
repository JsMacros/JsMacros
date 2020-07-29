package xyz.wagyourtail.jsmacros.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import xyz.wagyourtail.jsmacros.reflector.BossBarHelper;

public interface BossBarCallback {
    Event<BossBarCallback> EVENT = EventFactory.createArrayBacked(BossBarCallback.class,
            (listeners) -> (type, uuid, bossBar) -> {
                for (BossBarCallback event : listeners) {
                    event.interact(type, uuid, bossBar);
                }
            });
    void interact(String type, String uuid, BossBarHelper bossBar);
}
