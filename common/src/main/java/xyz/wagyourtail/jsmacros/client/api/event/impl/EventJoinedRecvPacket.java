package xyz.wagyourtail.jsmacros.client.api.event.impl;

import net.minecraft.network.packet.Packet;
import xyz.wagyourtail.jsmacros.client.api.helpers.PacketByteBufferHelper;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;
import xyz.wagyourtail.jsmacros.core.event.ICancelable;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@Event("JoinedRecvPacket")
@SuppressWarnings("unused")
public class EventJoinedRecvPacket implements BaseEvent, ICancelable {

    public boolean cancel;
    public Packet<?> packet;
    public final String type;

    public EventJoinedRecvPacket(Packet<?> packet) {
        this.packet = packet;
        this.type = PacketByteBufferHelper.getPacketName(packet);
        profile.triggerEventJoinNoAnything(this);
    }

    /**
     * After modifying the buffer, use {@link PacketByteBufferHelper#toPacket()} to get the modified
     * packet and replace this packet with the modified one.
     *
     * @return a helper for accessing and modifying the packet's data.
     * @since 1.8.4
     */
    public PacketByteBufferHelper getPacketBuffer() {
        return new PacketByteBufferHelper(packet);
    }

    @Override
    public void cancel() {
        this.cancel = true;
    }

    @Override
    public boolean isCanceled() {
        return cancel;
    }

    @Override
    public String toString() {
        return String.format("%s:{\"type\": \"%s\"}", this.getEventName(), type);
    }

}
