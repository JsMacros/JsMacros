package xyz.wagyourtail.jsmacros.client.api.event.impl;

import net.minecraft.network.Packet;

import xyz.wagyourtail.jsmacros.client.api.helpers.PacketByteBufferHelper;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;

import java.io.IOException;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@Event(value = "RecvPacket")
@SuppressWarnings("unused")
public class EventRecvPacket implements BaseEvent {

    public final Packet<?> packet;
    public final String type;

    public EventRecvPacket(Packet<?> packet) {
        this.packet = packet;
        this.type = PacketByteBufferHelper.getPacketName(packet);
        profile.triggerEventNoAnything(this);
    }

    /**
     * After modifying the buffer, use {@link PacketByteBufferHelper#toPacket()} to get the modified
     * packet and replace this packet with the modified one.
     *
     * @return a helper for accessing and modifying the packet's data.
     *
     * @since 1.8.4
     */
    public PacketByteBufferHelper getPacketBuffer() throws IOException {
        return new PacketByteBufferHelper(packet);
    }

    @Override
    public String toString() {
        return String.format("%s:{\"type\": \"%s\"}", this.getEventName(), type);
    }

}
