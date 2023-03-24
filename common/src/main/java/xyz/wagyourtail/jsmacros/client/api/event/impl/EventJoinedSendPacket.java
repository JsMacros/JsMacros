package xyz.wagyourtail.jsmacros.client.api.event.impl;


import net.minecraft.network.packet.Packet;
import xyz.wagyourtail.jsmacros.client.api.helpers.PacketByteBufferHelper;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;
import xyz.wagyourtail.jsmacros.core.event.ICancelable;
import xyz.wagyourtail.jsmacros.core.library.impl.FReflection;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@Event("JoinedSendPacket")
@SuppressWarnings("unused")
public class EventJoinedSendPacket implements BaseEvent, ICancelable {

    private static final FReflection REFLECTION = new FReflection(null);

    public boolean cancel;
    public Packet<?> packet;
    public final String type;

    public EventJoinedSendPacket(Packet<?> packet) {
        this.packet = packet;
        this.type = PacketByteBufferHelper.getPacketName(packet);
        profile.triggerEventJoinNoAnything(this);
    }

    /**
     * Replaces the packet of this event with a new one of the same type, created from the given
     * arguments. It's recommended to use {@link #getPacketBuffer()} to modify the packet instead.
     *
     * @param args the arguments to pass to the packet's constructor
     * @since 1.8.4
     */
    public void replacePacket(Object... args) {
        packet = REFLECTION.newInstance(packet.getClass(), args);
    }

    /**
     * After modifying the buffer, use {@link PacketByteBufferHelper#toPacket()} to get the modified
     * packet and replace this packet with the modified one.
     *
     * @return a helper for accessing and modifying the packet's data.
     *
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
