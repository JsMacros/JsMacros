package xyz.wagyourtail.jsmacros.client.api.event.impl;

import net.minecraft.network.Packet;

import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;
import xyz.wagyourtail.jsmacros.core.event.ICancelable;

/**
 * @author Etheradon
 * @since 1.9.0
 */
@Event("JoinedSendPacket")
public class EventJoinedSendPacket implements BaseEvent, ICancelable {

    public boolean cancel;
    public Packet<?> packet;

    public EventJoinedSendPacket(Packet<?> packet) {
        this.packet = packet;
        profile.triggerEventJoinNoAnything(this);
    }

    @Override
    public void cancel() {
        this.cancel = true;
    }

    @Override
    public boolean isCanceled() {
        return cancel;
    }
}
