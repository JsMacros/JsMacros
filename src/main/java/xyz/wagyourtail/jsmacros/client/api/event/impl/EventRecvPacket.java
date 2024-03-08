package xyz.wagyourtail.jsmacros.client.api.event.impl;

import net.minecraft.network.packet.Packet;
import org.jetbrains.annotations.Nullable;
import xyz.wagyourtail.doclet.DocletReplaceReturn;
import xyz.wagyourtail.jsmacros.client.api.helpers.PacketByteBufferHelper;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@Event(value = "RecvPacket", cancellable = true)
@SuppressWarnings("unused")
public class EventRecvPacket extends BaseEvent {
    @Nullable
    public Packet<?> packet;
    @DocletReplaceReturn("PacketName")
    public final String type;

    @SuppressWarnings("NullableProblems")
    public EventRecvPacket(Packet<?> packet) {
        this.packet = packet;
        this.type = PacketByteBufferHelper.getPacketName(packet);
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
    public String toString() {
        return String.format("%s:{\"type\": \"%s\"}", this.getEventName(), type);
    }

}
