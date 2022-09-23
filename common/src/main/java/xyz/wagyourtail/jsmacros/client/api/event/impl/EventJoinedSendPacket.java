package xyz.wagyourtail.jsmacros.client.api.event.impl;

import net.minecraft.network.Packet;

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

    public void replacePacket(Object... args) {
        packet = REFLECTION.newInstance(packet.getClass(), args);
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
