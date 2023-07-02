package xyz.wagyourtail.jsmacros.client.api.event.impl;

import net.minecraft.entity.Entity;
import xyz.wagyourtail.jsmacros.client.api.helpers.EntityHelper;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;

@Event("AttackEntity")
public class EventAttackEntity implements BaseEvent {
    public final EntityHelper<?> entity;

    public EventAttackEntity(Entity entity) {
        this.entity = EntityHelper.create(entity);

        profile.triggerEvent(this);
    }
}
