package xyz.wagyourtail.jsmacros.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.damage.DamageSource;

public interface DamageCallback {
    Event<DamageCallback> EVENT = EventFactory.createArrayBacked(DamageCallback.class,
            (listeners) -> (source, health, change) -> {
                for (DamageCallback event : listeners) {
                    event.interact(source, health, change);
                }
            });
    void interact(DamageSource source, float health, float change);
}
