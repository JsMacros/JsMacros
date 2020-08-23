package xyz.wagyourtail.jsmacros.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import xyz.wagyourtail.jsmacros.reflector.EntityHelper;

public interface SoundCallback {
    Event<SoundCallback> EVENT = EventFactory.createArrayBacked(SoundCallback.class,
            (listeners) -> (sound, volume, pitch, x, y, z, entity) -> {
                for (SoundCallback event : listeners) {
                    event.interact(sound, volume, pitch, x, y, z, entity);
                }
            });
    void interact(String sound, float volume, float pitch, double x, double y, double z, EntityHelper entity);
}
