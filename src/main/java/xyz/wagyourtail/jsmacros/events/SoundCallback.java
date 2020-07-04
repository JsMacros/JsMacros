package xyz.wagyourtail.jsmacros.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface SoundCallback {
    Event<SoundCallback> EVENT = EventFactory.createArrayBacked(SoundCallback.class,
            (listeners) -> (sound) -> {
                for (SoundCallback event : listeners) {
                    event.interact(sound);
                }
            });
    void interact(String sound);
}
