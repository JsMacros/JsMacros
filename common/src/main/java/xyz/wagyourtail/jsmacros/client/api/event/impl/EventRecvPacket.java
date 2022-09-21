package xyz.wagyourtail.jsmacros.client.api.event.impl;

import net.minecraft.network.Packet;

import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@Event(value = "RecvPacket")
public class EventRecvPacket implements BaseEvent {

    public final Packet<?> packet;

    public EventRecvPacket(Packet<?> packet) {
        this.packet = packet;
        profile.triggerEventNoAnything(this);
    }

}
