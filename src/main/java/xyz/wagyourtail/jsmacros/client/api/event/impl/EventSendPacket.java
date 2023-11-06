package xyz.wagyourtail.jsmacros.client.api.event.impl;

import net.minecraft.network.packet.Packet;
import org.jetbrains.annotations.Nullable;
import xyz.wagyourtail.doclet.DocletReplaceReturn;
import xyz.wagyourtail.jsmacros.client.api.helpers.PacketByteBufferHelper;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;
import xyz.wagyourtail.jsmacros.core.library.impl.FReflection;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@Event(value = "SendPacket", cancellable = true)
@SuppressWarnings("unused")
public class EventSendPacket extends BaseEvent {

    private static final FReflection REFLECTION = new FReflection(null);
    @Nullable
    public Packet<?> packet;
    @DocletReplaceReturn("PacketName")
    public final String type;

    @SuppressWarnings("NullableProblems")
    public EventSendPacket(Packet<?> packet) {
        this.packet = packet;
        this.type = PacketByteBufferHelper.getPacketName(packet);
    }

    /**
     * Replaces the packet of this event with a new one of the same type, created from the given
     * arguments. It's recommended to use {@link #getPacketBuffer()} to modify the packet instead.
     *
     * @param args the arguments to pass to the packet's constructor
     * @throws NullPointerException if this.packet is null
     * @since 1.8.4
     */
    public void replacePacket(Object... args) {
        //noinspection DataFlowIssue
        packet = REFLECTION.newInstance(packet.getClass(), args);
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
