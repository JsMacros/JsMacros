package xyz.wagyourtail.jsmacros.client.api.event.impl.player;

import net.minecraft.entity.Entity;
import xyz.wagyourtail.doclet.DocletReplaceReturn;
import xyz.wagyourtail.jsmacros.client.api.helper.world.entity.EntityHelper;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;

@Event("InteractEntity")
public class EventInteractEntity extends BaseEvent {
    public final boolean offhand;
    @DocletReplaceReturn("ActionResult")
    public final String result;
    public final EntityHelper<?> entity;

    public EventInteractEntity(boolean offhand, String resultStatus, Entity entity) {
        this.offhand = offhand;
        this.result = resultStatus;
        this.entity = EntityHelper.create(entity);
    }

    @Override
    public String toString() {
        return String.format("%s:{\"entity\": %s, \"result\": \"%s\"}", this.getEventName(), entity, result);
    }

}
