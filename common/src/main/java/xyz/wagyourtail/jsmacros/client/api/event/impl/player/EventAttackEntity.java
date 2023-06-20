package xyz.wagyourtail.jsmacros.client.api.event.impl.player;

import net.minecraft.entity.Entity;
import xyz.wagyourtail.jsmacros.client.api.helpers.world.entity.EntityHelper;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;

@Event("AttackEntity")
public class EventAttackEntity implements BaseEvent {
    public final EntityHelper<?> entity;

    public EventAttackEntity(Entity entity) {
        this.entity = EntityHelper.create(entity);

        profile.triggerEvent(this);
    }

    @Override
    public String toString() {
        return String.format("%s:{\"entity\": %s}", this.getEventName(), entity);
    }

}
