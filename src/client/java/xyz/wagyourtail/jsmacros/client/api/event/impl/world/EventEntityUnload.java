package xyz.wagyourtail.jsmacros.client.api.event.impl.world;

import net.minecraft.entity.Entity;
import xyz.wagyourtail.doclet.DocletReplaceReturn;
import xyz.wagyourtail.jsmacros.client.api.helpers.world.entity.EntityHelper;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;

@Event("EntityUnload")
public class EventEntityUnload extends BaseEvent {
    public final EntityHelper<?> entity;
    @DocletReplaceReturn("EntityUnloadReason")
    public final String reason;

    public EventEntityUnload(Entity e, Entity.RemovalReason reason) {
        this.entity = EntityHelper.create(e);
        this.reason = reason.toString();
    }

    @Override
    public String toString() {
        return String.format("%s:{\"entity\": %s, \"reason\": \"%s\"}", this.getEventName(), entity.toString(), reason);
    }

}
