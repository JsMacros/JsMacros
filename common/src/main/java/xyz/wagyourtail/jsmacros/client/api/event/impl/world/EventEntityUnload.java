package xyz.wagyourtail.jsmacros.client.api.event.impl.world;

import net.minecraft.entity.Entity;
import xyz.wagyourtail.jsmacros.client.api.helpers.world.entity.EntityHelper;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;

@Event("EntityUnload")
public class EventEntityUnload implements BaseEvent {
    public final EntityHelper<?> entity;

    public EventEntityUnload(Entity e) {
        this.entity = EntityHelper.create(e);

        profile.triggerEvent(this);
    }

    @Override
    public String toString() {
        return String.format("%s:{\"entity\": %s}", this.getEventName(), entity.toString());
    }
}
