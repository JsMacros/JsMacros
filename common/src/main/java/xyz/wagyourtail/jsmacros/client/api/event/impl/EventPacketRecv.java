package xyz.wagyourtail.jsmacros.client.api.event.impl;

import net.minecraft.network.Packet;

import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;

/**
 * @author Etheradon
 * @since 1.9.0
 */
@Event(value = "PacketRecv")
public class EventPacketRecv implements BaseEvent {

    public final Packet<?> packet;

    public EventPacketRecv(Packet<?> packet) {
        this.packet = packet;
        profile.triggerEvent(this);
    }

}
