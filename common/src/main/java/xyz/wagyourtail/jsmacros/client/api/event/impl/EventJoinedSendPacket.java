package xyz.wagyourtail.jsmacros.client.api.event.impl;

import net.minecraft.network.Packet;

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

    public EventJoinedSendPacket(Packet<?> packet) {
        this.packet = packet;
        profile.triggerEventJoinNoAnything(this);
    }

    /**
     * Replaces the packet of this event with a new one of the same type, created from the given
     * arguments.
     *
     * @param args the arguments to pass to the packet's constructor.
     * @since 1.8.4
     */
    public void replacePacket(Object... args) {
        packet = REFLECTION.newInstance(packet.getClass(), args);
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
    public void cancel() {
        this.cancel = true;
    }

    @Override
    public boolean isCanceled() {
        return cancel;
    }

    @Override
    public String toString() {
        return String.format("%s:{}", this.getEventName());
    }

}
