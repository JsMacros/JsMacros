package xyz.wagyourtail.jsmacros.client.api.event.impl;

import net.minecraft.network.Packet;

import xyz.wagyourtail.jsmacros.client.api.helpers.PacketByteBufferHelper;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;
import xyz.wagyourtail.jsmacros.core.event.ICancelable;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@Event("JoinedRecvPacket")
public class EventJoinedRecvPacket implements BaseEvent, ICancelable {

    public boolean cancel;
    public Packet<?> packet;

    public EventJoinedRecvPacket(Packet<?> packet) {
        this.packet = packet;
        profile.triggerEventJoinNoAnything(this);
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
