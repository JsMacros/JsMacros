package xyz.wagyourtail.jsmacros.client.api.event.impl;

import net.minecraft.entity.Entity;
import xyz.wagyourtail.jsmacros.client.api.helpers.entity.EntityHelper;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;

/**
 * @author FlareStormGaming
 * @since 1.6.5
 */

@Event("EntityHealed")
public class EventEntityHealed implements BaseEvent {
    public final EntityHelper<?> entity;
    public final float health;
    public final float damage;

    public EventEntityHealed(Entity e, float health, float amount) {
        entity = EntityHelper.create(e);
        this.health = health;
        this.damage = amount;

        profile.triggerEvent(this);
    }

    public String toString() {
        return String.format("%s:{\"entity\": %s, \"health\": %s, \"damage\": %f}", this.getEventName(), entity.toString(), health, damage);
    }
}
