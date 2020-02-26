package xyz.wagyourtail.jsmacros.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.ActionResult;

public interface KeyCallback {
    Event<KeyCallback> EVENT = EventFactory.createArrayBacked(KeyCallback.class, 
        (listeners) -> (window, key, scancode, action, mods) -> {
            for (KeyCallback event : listeners) {
                ActionResult result = event.interact(window, key, scancode, action, mods);
                if (result != ActionResult.PASS) {
                    return result;
                }
            }
            return ActionResult.PASS;
        });
    
    ActionResult interact(long window, int key, int scancode, int action, int mods);
}