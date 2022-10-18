package xyz.wagyourtail.jsmacros.client.api.event.impl;

import net.minecraft.entity.Entity;
import xyz.wagyourtail.jsmacros.client.api.helpers.EntityHelper;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;

@Event("InteractEntity")
public class EventInteractEntity implements BaseEvent {
    public final boolean offhand;
    public final String result;
    public final EntityHelper<?> entity;

    public EventInteractEntity(boolean offhand, String resultStatus, Entity entity) {
        this.offhand = offhand;
        this.result = resultStatus;
        this.entity = EntityHelper.create(entity);

        profile.triggerEvent(this);
    }

    @Override
    public String toString() {
        return String.format("%s:{\"entity\": %s, \"result\": \"%s\"}", this.getEventName(), entity, result);
    }
}
