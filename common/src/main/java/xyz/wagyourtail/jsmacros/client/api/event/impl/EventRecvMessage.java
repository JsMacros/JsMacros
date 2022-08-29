package xyz.wagyourtail.jsmacros.client.api.event.impl;

import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import xyz.wagyourtail.jsmacros.client.api.helpers.TextHelper;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;

/**
 * @author Wagyourtail
 * @since 1.2.7
 */
 @Event(value = "RecvMessage", oldName = "RECV_MESSAGE")
public class EventRecvMessage implements BaseEvent {
    public TextHelper text;

    /**
     * @since 1.8.2
     */
    public byte @Nullable [] signature;

    /**
     * @since 1.8.2
     */
    @Nullable
    public String messageType;

    public EventRecvMessage(Text message) {
        this.text = new TextHelper(message);


        profile.triggerEventJoinNoAnything(this);
    }
    
    public String toString() {
        return String.format("%s:{\"text\": %s, \"signature\": %s, \"messageType\": \"%s\"}", this.getEventName(), text, signature != null && signature.length > 0, messageType);
    }
}
