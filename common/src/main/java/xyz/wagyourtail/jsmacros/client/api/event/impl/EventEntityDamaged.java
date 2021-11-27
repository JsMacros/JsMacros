package xyz.wagyourtail.jsmacros.client.api.event.impl;

import net.minecraft.entity.Entity;
import xyz.wagyourtail.jsmacros.client.api.helpers.EntityHelper;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;

@Event("EntityDamaged")
public class EventEntityDamaged implements BaseEvent {
    public final EntityHelper<?> entity;
    public final float damage;

    public EventEntityDamaged(Entity e, float amount) {
        entity = EntityHelper.create(e);
        this.damage = amount;

        profile.triggerEvent(this);
    }

    public String toString() {
        return String.format("%s:{\"entity\": %s, \"damage\": %f}", this.getEventName(), entity.toString(), damage);
    }
}
