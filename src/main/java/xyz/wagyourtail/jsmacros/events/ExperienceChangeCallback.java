package xyz.wagyourtail.jsmacros.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface ExperienceChangeCallback {
    Event<ExperienceChangeCallback> EVENT = EventFactory.createArrayBacked(ExperienceChangeCallback.class,
            (listeners) -> (prog, total, level) -> {
                for (ExperienceChangeCallback event : listeners) {
                    event.interact(prog, total, level);
                }
            });
    void interact(float progress, int total, int level);
}
