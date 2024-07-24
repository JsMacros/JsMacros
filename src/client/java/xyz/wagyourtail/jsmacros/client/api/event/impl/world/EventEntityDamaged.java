package xyz.wagyourtail.jsmacros.client.api.event.impl.world;

import net.minecraft.entity.Entity;
import xyz.wagyourtail.jsmacros.client.api.helpers.world.entity.EntityHelper;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;

@Event("EntityDamaged")
public class EventEntityDamaged extends BaseEvent {
    public final EntityHelper<?> entity;
    /**
     * @since 1.6.5
     */
    public final float health;
    public final float damage;

    public EventEntityDamaged(Entity e, float health, float amount) {
        entity = EntityHelper.create(e);
        this.health = health;
        this.damage = amount;
    }

    @Override
    public String toString() {
        return String.format("%s:{\"entity\": %s, \"health\": %f, \"damage\": %f}", this.getEventName(), entity.toString(), health, damage);
    }

}
