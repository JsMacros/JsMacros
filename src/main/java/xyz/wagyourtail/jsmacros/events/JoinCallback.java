package xyz.wagyourtail.jsmacros.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import xyz.wagyourtail.jsmacros.reflector.ClientPlayerEntityHelper;

public interface JoinCallback {
    Event<JoinCallback> EVENT = EventFactory.createArrayBacked(JoinCallback.class, 
        (listeners) -> (address, player) -> {
            for (JoinCallback event : listeners) {
                event.interact(address, player);
            }
    });
    
    void interact(String address, ClientPlayerEntityHelper player);
}
