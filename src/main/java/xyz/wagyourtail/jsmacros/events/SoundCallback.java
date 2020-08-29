package xyz.wagyourtail.jsmacros.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface SoundCallback {
    Event<SoundCallback> EVENT = EventFactory.createArrayBacked(SoundCallback.class,
            (listeners) -> (sound, volume, pitch, x, y, z) -> {
                for (SoundCallback event : listeners) {
                    event.interact(sound, volume, pitch, x, y, z);
                }
            });
    void interact(String sound, float volume, float pitch, double x, double y, double z);
}
