package xyz.wagyourtail.jsmacros.client.api.event.impl;

import net.minecraft.entity.player.ClientPlayerEntity;
import xyz.wagyourtail.jsmacros.client.api.helpers.ClientPlayerEntityHelper;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;

/**
 * @author Wagyourtail
 * @since 1.2.7
 */
 @Event(value = "JoinServer", oldName = "JOIN_SERVER")
public class EventJoinServer implements BaseEvent {
    public final ClientPlayerEntityHelper<ClientPlayerEntity> player;
    public final String address;

    public EventJoinServer(ClientPlayerEntity player, String address) {
        this.player = new ClientPlayerEntityHelper<>(player);
        this.address = address;
        profile.triggerEvent(this);
    }

    public String toString() {
        return String.format("%s:{\"address\": \"%s\"}", this.getEventName(), address);
    }
}
