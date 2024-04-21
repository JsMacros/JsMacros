package xyz.wagyourtail.jsmacros.client.api.event.filterer;

import org.jetbrains.annotations.Nullable;
import xyz.wagyourtail.doclet.DocletReplaceParams;
import xyz.wagyourtail.doclet.DocletReplaceReturn;
import xyz.wagyourtail.jsmacros.client.api.event.impl.EventRecvPacket;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.EventFilterer;

/**
 * @author aMelonRind
 * @since 1.9.1
 */
@SuppressWarnings("unused")
public class FiltererRecvPacket implements EventFilterer {
    @Nullable
    @DocletReplaceReturn("PacketName | null")
    public String type;

    @Override
    public boolean canFilter(String event) {
        return "RecvPacket".equals(event);
    }

    @Override
    public boolean test(BaseEvent event) {
        return (event instanceof EventRecvPacket e) && (type == null || e.type.equals(type));
    }

    @DocletReplaceParams("type: PacketName | null")
    public FiltererRecvPacket setType(@Nullable String type) {
        this.type = type;
        return this;
    }

}
