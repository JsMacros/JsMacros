package xyz.wagyourtail.jsmacros.client.api.event.impl;

import net.minecraft.network.Packet;

import xyz.wagyourtail.jsmacros.client.api.helpers.PacketByteBufferHelper;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@Event(value = "SendPacket")
public class EventSendPacket implements BaseEvent {

    public final Packet<?> packet;

    public EventSendPacket(Packet<?> packet) {
        this.packet = packet;
        profile.triggerEventNoAnything(this);
    }

    /**
     * @return a helper for accessing and modifying the packet's data.
     *
     * @since 1.8.4
     */
    public PacketByteBufferHelper getPacketBuffer() {
        return new PacketByteBufferHelper(packet);
    }

    @Override
    public String toString() {
        return String.format("%s:{}", this.getEventName());
    }
    
}
