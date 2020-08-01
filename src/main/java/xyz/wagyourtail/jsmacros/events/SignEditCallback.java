package xyz.wagyourtail.jsmacros.events;

import java.util.List;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface SignEditCallback {
    Event<SignEditCallback> EVENT = EventFactory.createArrayBacked(SignEditCallback.class,
            (listeners) -> (signText, x, y, z) -> {
                for (SignEditCallback event : listeners) {
                    if (event.interact(signText, x, y, z)) return true;
                }
                return false;
            });
    boolean interact(List<String> signText, int x, int y, int z);
}
