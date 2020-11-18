package xyz.wagyourtail.jsmacros.api.events;

import net.minecraft.client.network.ClientPlayerEntity;
import xyz.wagyourtail.jsmacros.api.helpers.ClientPlayerEntityHelper;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;

/**
 * @author Wagyourtail
 * @since 1.2.7
 */
 @Event(value = "JoinServer", oldName = "JOIN_SERVER")
public class EventJoinServer implements BaseEvent {
    public final ClientPlayerEntityHelper player;
    public final String address;
    
    public EventJoinServer(ClientPlayerEntity player, String address) {
        this.player = new ClientPlayerEntityHelper(player);
        this.address = address;
        profile.triggerMacro(this);
    }

    public String toString() {
        return String.format("%s:{\"address\": \"%s\"}", this.getEventName(), address);
    }
}
